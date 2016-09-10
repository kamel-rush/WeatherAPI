package com.GC.APIController;

import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	@RequestMapping(value = "getweather", method = RequestMethod.GET)
	public String getWeather(Model model) {
		try {
			HttpClient http = HttpClientBuilder.create().build();

			// address that we want to call
			HttpHost host = new HttpHost("forecast.weather.gov", 80, "http");
			// http method: get
			HttpGet getPage = new HttpGet("/MapClick.php?lat=38.4247341&lon=-86.9624086&FcstType=xml");

			//getPage.setHeader("X-Mashape-Key", "put key here");

			
			// execute the http request and get the http response back
			HttpResponse resp = http.execute(host, getPage);

			// String result = EntityUtils.toString(resp.getEntity());

			String result = "";
			String xmlString = EntityUtils.toString(resp.getEntity());
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = factory.newDocumentBuilder();
			
			InputSource inStream = new InputSource();
			inStream.setCharacterStream(new StringReader(xmlString));
			Document doc = db.parse(inStream);
			
			

			String weatherForeCast = "empty";
			NodeList nl = doc.getElementsByTagName("text");
			for (int i = 0; i < nl.getLength(); i++) {

				org.w3c.dom.Element nameElement = (org.w3c.dom.Element) nl.item(i);
				weatherForeCast = nameElement.getFirstChild().getNodeValue().trim();
				result += ("<h6>" + weatherForeCast + "</h6>");

			}

			model.addAttribute("pagedata", result);
		} catch (Exception e) {
			return "errorpage";
		}
		return "weather";

	}

}
