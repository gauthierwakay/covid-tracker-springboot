package io.nspai.Covidtracker.controllers;

import io.nspai.Covidtracker.models.LocationStats;
import io.nspai.Covidtracker.services.CoronavirusDataServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @ gauthier Wakay
 * In order to render anything in show up in the access Url in html you need a Controller
 * We can use the annotation stereotype of RestController tp tell that for all method of this controller
 * they should render Rest responses JSON format but we want Html page that why we are creating just a controller.
 * use the GetMapping method to map your html with the Root Application
 */
@Controller
public class Homecontroller {

    /**
     * this is a concept of adding a model in the response and this can be fetched in the Html pages later
     * the attribute created here will be accessed in the html by using thymeleaf syntax.
     * the attribute name will be accessible on the html page.
     * @param model
     * @return
     *
     * here I will pass the data service since I'm passing the service I have to use  @autowired in the services to access the data
     */

    @Autowired
    CoronavirusDataServices coronavirusDataServices;

    @GetMapping("/") // and this has to work because of thymeleaf dependency in the URL
    public String home(Model model){
        List<LocationStats> allStats = coronavirusDataServices.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();

        model.addAttribute("locationStats", allStats); // here I'm placing an attribute in a the model and pass any value i need here.
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);// this attribute is the same as the one given html response
        return "home"; // by creating this home here we have to reference to an html page.
    }
}
