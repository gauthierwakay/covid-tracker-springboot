package io.nspai.Covidtracker.services;


import io.nspai.Covidtracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service // this tells just spring that our class is a spring service
public class CoronavirusDataServices {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats =new ArrayList<>();

    //allstats being a private value we need the getter for this variable.


    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron ="* * 1 * * * ")
    //PostConstruct this annotation tells spring after constructing the services Just run this method
    public void fetchVirusData() throws IOException, InterruptedException {

        List<LocationStats> newStats =new ArrayList<>();
        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats(); // this is an object of the model to call the class
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));


            int latestTotalCases = Integer.parseInt(record.get(record.size() - 1));
            int previousDayCases = Integer.parseInt(record.get(record.size() - 2));

            locationStat.setLatestTotalCases(latestTotalCases);
            locationStat.setDiffFromPreviousDay(latestTotalCases - previousDayCases);
            //String state = record.get("Province/State"); // this line was to test if we could align the data in a columns
            //System.out.println(state);
            //System.out.println(locationStat); //this print the data
            newStats.add(locationStat);
        }
            this.allStats = newStats; // here at the end of the loop we are moving the results to the list

        }
    }
