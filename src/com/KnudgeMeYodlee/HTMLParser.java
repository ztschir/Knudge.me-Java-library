package com.KnudgeMeYodlee;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class HTMLParser {
    public HTMLParser(String unparsedHTML){
    	Document soupParser = Jsoup.parse(unparsedHTML);
    	
    	Element loginElement = soupParser.getElementById("login");
    	System.out.println(loginElement.val());
    }
}
