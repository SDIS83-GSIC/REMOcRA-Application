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
            .requireRelNofollowOnLinks()
            .toFactory()
    }
}
