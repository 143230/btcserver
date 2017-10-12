package com.btc.app.spider.htmlunit;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.host.Window;

public class DimissLogJavaScriptEnginee extends JavaScriptEngine {
    /**
     * Creates an instance for the specified {@link WebClient}.
     *
     * @param webClient the client that will own this engine
     */
    public DimissLogJavaScriptEnginee(WebClient webClient) {
        super(webClient);
    }

    @Override
    protected void handleJavaScriptException(ScriptException scriptException, boolean triggerOnError) {
        final HtmlPage page = scriptException.getPage();
        if (triggerOnError && page != null) {
            final WebWindow window = page.getEnclosingWindow();
            if (window != null) {
                final Window w = (Window) window.getScriptableObject();
                if (w != null) {
                    try {
                        w.triggerOnError(scriptException);
                    }
                    catch (final Exception e) {
                        handleJavaScriptException(new ScriptException(page, e, null), false);
                    }
                }
            }
        }
        getWebClient().getJavaScriptErrorListener().scriptException(page, scriptException);
        // Throw a Java exception if the user wants us to.
        if (getWebClient().getOptions().isThrowExceptionOnScriptError()) {
            throw scriptException;
        }

    }
}
