package info.opencards.md

/**
 * @author Holger Brandl
 */


val downscaleImg = """
    <style type="text/css">
        img {
            max-width: 100%;
        }
    </style>
""".trimIndent()

// from https://codepen.io/P3R0/pen/jEXvRK

val blockQuote = """
        <style type="text/css">
blockquote {
  background: #e9e9e9;
  border-left: 10px solid #ccc;
  margin: 1.5em 10px;
  padding: 0.5em 10px;
  quotes: "";
}
blockquote:before {
  color: #ccc;
  font-size: 4em;
  line-height: 0.1em;
  margin-right: 0.25em;
  vertical-align: -0.4em;
}
blockquote p {
  display: inline;
}
    </style>

""".trimIndent()