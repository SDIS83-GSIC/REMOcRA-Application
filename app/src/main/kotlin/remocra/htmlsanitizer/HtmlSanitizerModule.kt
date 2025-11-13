package remocra.htmlsanitizer

import com.google.inject.Provides
import com.google.inject.Singleton
import org.owasp.html.HtmlPolicyBuilder
import org.owasp.html.PolicyFactory
import remocra.RemocraModule

object HtmlSanitizerModule : RemocraModule() {

    @Provides
    @Singleton
    fun providePolicyFactory(): PolicyFactory {
        return HtmlPolicyBuilder()
            .allowCommonInlineFormattingElements()
            .allowCommonBlockElements()
            .allowStandardUrlProtocols()
            .allowElements("a")
            .allowAttributes("href", "target")
            .onElements("a")
            .allowElements("img")
            .allowAttributes("src", "alt", "title", "width", "height").onElements("img")
            .allowElements("iframe")
            .allowAttributes("src", "width", "height", "title", "frameborder", "allow", "allowfullscreen")
            .onElements("iframe")
            .allowAttributes("src").matching { _, _, url ->
                if (url != null && (
                        url.startsWith("https://www.youtube.com/")
                        )
                ) {
                    url
                } else {
                    null
                }
            }.onElements("iframe")
            .allowAttributes("style").onElements("div", "section", "a", "span", "p", "img", "h1", "h2", "h3", "h4")
            .requireRelNofollowOnLinks()
            .toFactory()
    }
}
