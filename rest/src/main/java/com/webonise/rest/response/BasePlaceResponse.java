package com.webonise.rest.response;

import java.util.Collections;
import java.util.List;

public class BasePlaceResponse {

    public String status;
    public List<String> html_attributions;

    BasePlaceResponse() {
        status = "";
        html_attributions = Collections.emptyList();
    }

}
