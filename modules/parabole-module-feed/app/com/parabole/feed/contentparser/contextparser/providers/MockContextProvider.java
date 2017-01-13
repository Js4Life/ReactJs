package com.parabole.feed.contentparser.contextparser.providers;

import com.parabole.feed.contentparser.contextparser.models.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajdeep on 06-Jan-17.
 */
public class MockContextProvider {
    private List<Context> contextList = new ArrayList<>();

    public MockContextProvider() {
        generateContextList();
    }

    private void generateContextList() {
        Context context1 = new Context();

        context1.setSubject("interest rate");
        context1.setPredicate("for a loan");
        context1.setObject("Hedging");
        context1.setContextTag("Context 1");
        contextList.add(context1);

        Context context2 = new Context();

        context2.setSubject("original contractual rate");
        context2.setPredicate(", not the rate was specified in the");
        context2.setObject("restructuring agreement");
        context2.setContextTag("Context 2");
        contextList.add(context2);

        Context context3 = new Context();

        context3.setSubject("discount");
        context3.setPredicate("expected");
        context3.setObject("future cash flows");
        context3.setContextTag("Context 3");
        contextList.add(context3);

        Context context4 = new Context();

        context4.setSubject("interest rate");
        context4.setPredicate("used to");
        context4.setObject("discount");
        context4.setContextTag("Context 4");
        contextList.add(context4);
    }

    public List<Context> getContextList(){
        return contextList;
    }
}
