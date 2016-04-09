package info.opencards.md

import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.CompositeASTNode
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.LinkMap
import org.intellij.markdown.parser.MarkdownParser
import java.io.File
import java.io.IOException
import java.net.URI
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    //        String text= "## hellp\n" +
    //                "content" +
    //                "\n" +
    //                "### other section\n" +
    //                "\n" +
    //                "more conetnt\n";

    val text = readFile("/Users/holger/projects/opencards/oc2/testdata/kotlin_qa.md", Charset.defaultCharset())

    parseMD(File("/Users/holger/projects/opencards/oc2/testdata/kotlin_qa.md"))
}


data class MarkdownFlashcard(val question: String, val answer: String)

fun parseMD(file: File): List<MarkdownFlashcard> {
    val text = readFile(file.absolutePath, Charset.defaultCharset())

    val markdownParser = MarkdownParser(GFMFlavourDescriptor())
    val parsedTree = markdownParser.buildMarkdownTreeFromString(text)


    parsedTree.children[0]


    val html = makeHtml(parsedTree, text, file.toURI())
    System.err.println("html is :\n" + html)

    parsedTree.children.filter { it is CompositeASTNode }.forEach { println("$it : ${makeHtml(it, text, file.toURI())}") }

    var blockCounter = 0
    val sections = parsedTree.children.map { makeHtml(it, text, file.toURI()) }.groupBy {
        if (it.startsWith("<h")) {
            blockCounter += 1
        }

        blockCounter
    }.map { it.value }

    // check if some are tagged as [qa]
    val isQA = sections.find { it.first().contains("[qa]") } != null


    // try to extract the questions
    val cards = sections.
            // rermove empty sections
            filter { it.size > 1 }.

            // remove non-QA elements when being in qa mode
            filter { isQA && it.first().contains("[qa]") }.

            // create question to answer map
            map {
                // normalize question size to be h2 and remove optional [qa] in header
                val question = it.first().replace("[qa]", "").replace("<h[1234]".toRegex(), "<h1").replace("h[1234]>".toRegex(), "h1>").trim()
                MarkdownFlashcard(question, it.drop(1).joinToString("\n"))
            }

    return cards
}

internal fun makeHtml(parsedTree: ASTNode, text: String, baseURI: URI): String {
    val htmlGeneratingProviders = GFMFlavourDescriptor().createHtmlGeneratingProviders(LinkMap.buildLinkMap(parsedTree, text), baseURI)

    val html = HtmlGenerator(text, parsedTree, htmlGeneratingProviders, true).generateHtml()
    return html
}

@Throws(IOException::class)
internal fun readFile(path: String, encoding: Charset): String {
    val encoded = Files.readAllBytes(Paths.get(path))
    return String(encoded, encoding)
}

