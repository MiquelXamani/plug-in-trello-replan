package web.controllers.APITrello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


/**
 * Created by Miquel on 15/02/2017.
 */

@Controller
@RequestMapping("/webhooks")
public class WebhookController {

    @RequestMapping(method= RequestMethod.GET)
    public ResponseEntity<String> checkG(){
        System.out.println("Trello checked!!");
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /*@RequestMapping(method= RequestMethod.HEAD)
    public HttpStatus checkH(){
        System.out.println("Trello checked!!");
        return HttpStatus.OK;
    }*/

    @RequestMapping(method= RequestMethod.POST)
    public ResponseEntity<String> notify(@RequestBody Map<String,Object> payload) {
        System.out.println("Trello notified me!!");
        return new ResponseEntity<String>(HttpStatus.OK);
    }
}