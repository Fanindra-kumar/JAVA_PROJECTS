package com.kushwaha;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.TimeZone;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MyServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		//		doGet(request, response);
		//		API setup
		String apiKey="95920c6e3ac65b1ef639cd5003c3f098";
		//		GEt city from input
		String city = request.getParameter("city");

		//		Creating the url for the openWeather API request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
		try {
			//		API Integration
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			//		Reading the data from network
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);

			//		want to store in string
			StringBuilder responseContent = new StringBuilder();

			Scanner s = new Scanner(reader);
			while(s.hasNext()) {
				responseContent.append(s.nextLine());
			}
			s.close();

			//		TypeCasting ----> Parsing the data into JSON
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);


			//Date & Time
			long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
			Date date = new Date(dateTimestamp);
			SimpleDateFormat formatter = new SimpleDateFormat("EE, MMM dd, yyyy hh:mm:ss a z");
			formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			String formattedDate = formatter.format(date);
//			System.out.println(formattedDate);
//			System.out.println(date);

			//Temperature
			double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int temperatureCelsius = (int) (temperatureKelvin - 273.15);

			//Humidity
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

			//Wind Speed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

			//Weather Condition
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

			// Set the data as request attributes (for sending to the jsp page)
			request.setAttribute("date", formattedDate);
			request.setAttribute("city", city);
			request.setAttribute("temperature", temperatureCelsius);
			request.setAttribute("weatherCondition", weatherCondition); 
			request.setAttribute("humidity", humidity);    
			request.setAttribute("windSpeed", windSpeed);
			request.setAttribute("weatherData", responseContent.toString());

			connection.disconnect();

		}catch (IOException e) {
			e.printStackTrace();
		}
		// Forward the request to the weather.jsp page for rendering
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}
