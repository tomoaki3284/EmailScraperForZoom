package com.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.List;

public class EmailScraper {
	private String meetingId;
	private String password;
	
	public EmailScraper() {
		// TODO: Insert the URL of Gmail inside of double quote
		this("http://www.tomogmailtemplate.com.s3-website-us-east-1.amazonaws.com/");
	}
	
	public String getMeetingId() {
		return meetingId;
	}
	
	public String getPassword() {
		return password;
	}
	
	public EmailScraper(String url) {
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		
		try{
			HtmlPage page = client.getPage(url);
			List<HtmlElement> textDivs = page.getByXPath("//div");
			
			String idMatcher = "Meeting ID";
			String passMatcher = "password";
			
			HtmlElement meetingIdDiv = findMatcherDiv(textDivs, idMatcher);
			HtmlElement passwordDiv = findMatcherDiv(textDivs, passMatcher);
			if(null == meetingIdDiv){
				System.out.println("Meeting ID div not found");
			}
			else{
				meetingId = extractMeetingId(meetingIdDiv);
				String rawPassword = passwordDiv.getTextContent();
				String[] texts = rawPassword.split(" ");
				password = texts[texts.length-1];
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Meeting ID: " + meetingId);
		System.out.println("Password: " + password);
	}
	
	/**
	 * TODO: extract zoom id
	 *
	 * Format example: "Meeting ID: 993 3471 4883"
	 * We can split this formattedString by " "(space), then get last 3 text
	 *
	 * @param meetingIdDiv
	 * @return
	 */
	private String extractMeetingId(HtmlElement meetingIdDiv) {
		String[] rawTexts = meetingIdDiv.getTextContent().split(" ");
		int len = rawTexts.length;
		String result = rawTexts[len-3] + rawTexts[len-2] + rawTexts[len-1];
		return result;
	}
	
	
	private HtmlElement findMatcherDiv(List<HtmlElement> divs, String textMatcher) {
		String matcher = textMatcher.toLowerCase();
		int matcherLen = matcher.length();
		for(HtmlElement div : divs){
			String rawText = div.getTextContent();
			if(rawText == null || rawText.length() < matcherLen) continue;
			
			String text = rawText.substring(0,matcherLen).toLowerCase();
			if(matcher.equals(text)){
				return div;
			}
		}
		
		return null;
	}
}
