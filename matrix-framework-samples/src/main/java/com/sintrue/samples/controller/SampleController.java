package com.sintrue.samples.controller;

import com.sintrue.samples.api.ISampleRequest;
import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {
    private final SampleService sampleService;

    @Inject
    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    /**
     * Add the implementation class of the interface into the json request body
     * <pre>{@code
     * {
     *     "className": "com.sintrue.samples.api.SampleRequest",
     *     "sampleName": "Sample",
     *     "sampleJson": {
     *         "jsonName": "Json's name",
     *         "jsonAge": 18
     *     },
     *     "sampleExtended": [
     *         {
     *             "columnName": "extended_name",
     *             "columnValue": "Extended's name"
     *         },
     *         {
     *             "columnName": "extended_age",
     *             "columnValue": "18"
     *         }
     *     ]
     * }
     * }</pre>
     *
     * @param request json request
     * @return response
     */
    @PostMapping("/create")
    public SampleResponse create(@RequestBody ISampleRequest request) {
        return sampleService.create(request);
    }

}
