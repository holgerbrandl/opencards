package info.opencards.md

import info.opencards.Utils
import info.opencards.ui.preferences.AdvancedSettings.*
import org.intellij.markdown.ast.ASTNode
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
    //    parseMD(File("/Users/holger/projects/opencards/oc2/testdata/markdown/kotlin_qa.md"))

    val exampleMarkdown = """
        ## This is a header

        blabla

        >  some citation

    """.trimIndent()

    val markdownParser = MarkdownParser(GFMFlavourDescriptor())
    val parsedTree = markdownParser.buildMarkdownTreeFromString(exampleMarkdown)

    val htmlGeneratingProviders = GFMFlavourDescriptor().createHtmlGeneratingProviders(LinkMap.buildLinkMap(parsedTree, exampleMarkdown), null)

    val html = HtmlGenerator(exampleMarkdown, parsedTree, htmlGeneratingProviders, true).generateHtml()
    print(html)
}


data class MarkdownFlashcard(val question: String, val answer: String)

fun parseMD(file: File, useSelector: Boolean = false): List<MarkdownFlashcard> {
    val text = readFile(file.absolutePath, Charset.defaultCharset())

    val markdownParser = MarkdownParser(GFMFlavourDescriptor())
    val parsedTree = markdownParser.buildMarkdownTreeFromString(text)

    //    parsedTree.children[0]


    //    val html = makeHtml(parsedTree, text, file.toURI())
    //    System.err.println("html is :\n" + html)
    //    parsedTree.children.filter { it is CompositeASTNode }.forEach { println("$it : ${makeHtml(it, text, file.toURI())}") }

    var blockCounter = 0
    val sections = parsedTree.children.map { makeHtml(it, text, file.toURI()) }.groupBy {
        if (it.contains("^<h[123]{1}".toRegex())) {
            blockCounter += 1
        }

        blockCounter
    }.map { it.value }

    // check if some are tagged as [qa]
    //    val qaSelector = "[qa]"

    // see http://stackoverflow.com/questions/1140268/how-to-escape-a-square-bracket-for-pattern-compilation
    //    val qaSelector = Pattern.quote("[qa]")
    //    val qaSelector= "\\Q[qa]\\E".toRegex()
    val qaSelector = Utils.getPrefs().get(MARKDOWN_QA_SELECTOR, MARKDOWN_QA_SELECTOR_DEFAULT).toRegex()

    // we could auto-detect selector usage here but since there is a checkbox for it we better don't do so
    //    val isQA = useSelector && sections.find { it.first().contains(qaSelector) } != null
    val isQA = useSelector //&& sections.find { it.first().contains(qaSelector) } != null


    //  Slide title example  |<h2 md-src-pos="0..28">best question ever4? [qa]</h2>
    //  Slide title example  <h2 md-src-pos="48..66">what is kotlin?</h2>

    val removeSelectorFromTitle = Utils.getPrefs().getBoolean(MARKDOWN_REMOVE_SELECTOR, MARKDOWN_REMOVE_SELECTOR_DEFAULT)

    // try to extract the questions
    val cards = sections.
            // rermove empty sections
            filter { it.size > 1 }.

            // remove non-QA elements when being in qa mode
            filter { !isQA || it.first().contains(qaSelector) }.

            // create question to answer map
            map {
                var question = it.first()

                // optionally remove selector
                question = if (removeSelectorFromTitle) question.replace(qaSelector, "") else question

                // normalize question size to be h2
                question = question.replace("<h[1234]".toRegex(), "<h1").replace("h[1234]>".toRegex(), "h1>").trim()

                val answer = it.drop(1).takeWhile { !it.startsWith("<hr") }.joinToString("\n")

                MarkdownFlashcard(question, answer)
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

