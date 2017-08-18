package com.work.agent.services.impl;

import com.work.agent.model.Account;
import com.work.agent.model.AttorneyResponse;
import com.work.agent.model.Client;
import com.work.agent.model.LoanRequest;
import com.work.agent.services.IClientService;
import com.work.agent.services.ILoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class LoanService implements ILoanService {

    private final RestTemplate restTemplate;
    private final IClientService clientService;

    @Autowired
    public LoanService(RestTemplateBuilder restTemplateBuilder, IClientService clientService) {
        this.restTemplate = restTemplateBuilder.build();
        this.clientService = clientService;
    }

    @Override
    public AttorneyResponse requestLoan(final Client client, final Long amount) {
        return this.restTemplate.exchange("http://localhost:" + 8888 + "/verify",
                HttpMethod.POST,
                new HttpEntity<>(new LoanRequest(client.getAccount().getBalance(), amount),
                                new HttpHeaders() {{ add(HttpHeaders.CONTENT_TYPE, "application/json"); }}),
                AttorneyResponse.class)
                .getBody();

//        return this.restTemplate.exchange(
//                RequestEntity.post(URI.create("http://localhost:" + 8888 + "/verify"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(new LoanRequest(client.getAccount().getBalance(), amount)),
//                AttorneyResponse.class)
//                .getBody();
    }

    @Override
    public Account submitLoan(final Client client, final Long amount) {
        client.getAccount().addLoan(amount);
        clientService.update(client);
        return client.getAccount();
    }

    @Override
    public AttorneyResponse requestLoan(final Client client, final Integer amount) {
        return requestLoan(client, (long) amount);
    }

    @Override
    public Account submitLoan(final Client client, final Integer amount) {
        return submitLoan(client, (long) amount);
    }
}
