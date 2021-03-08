package com.carshopping.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;




@RestController
public class CarShoppingController {

    private static final Logger logger = LoggerFactory.getLogger(CarShoppingController.class);

    @GetMapping("/cars/{ID}")
    @ResponseBody
    public ResponseEntity<String> getCarbyID(@PathVariable("ID") String carid) {

        logger.debug("GET request for retrieving record for the id", carid);
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("json/warehouses.json");
        String strjson = null;

        try {
            strjson = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
            JSONArray obj = new JSONArray(strjson);
            //TODO : Need to remove hard coded index:
            JSONObject warehouse = obj.getJSONObject(0);
            JSONObject carObjlst = warehouse.getJSONObject(("cars"));
            JSONObject carObj = null;
            if (carid.equalsIgnoreCase("undefined")) {
                strjson = carObjlst.toString();
            }
            else {
                JSONArray vehicels = carObjlst.getJSONArray("vehicles");
                for(int index = 0; index < vehicels.length() ; index++) {
                    JSONObject tempcarObj = (JSONObject) vehicels.opt(index);
                    if (tempcarObj.getString("_id").equalsIgnoreCase(carid)) {
                        carObj = tempcarObj;
                        strjson = tempcarObj.toString();
                        break;
                    }
                }
                if (carObj == null) {
                    return new ResponseEntity<String>("{Record Not Found}", HttpStatus.NOT_FOUND); 
                }
            }
            logger.debug("Car details", strjson);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return new ResponseEntity<String>("{Internal Server Error}" ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(strjson, HttpStatus.OK);
    }


}
