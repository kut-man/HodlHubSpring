package com.example.hodlhub.controllers;

import com.example.hodlhub.dto.request.RequestTransactionDTO;
import com.example.hodlhub.models.Transaction;
import com.example.hodlhub.security.HolderDetails;
import com.example.hodlhub.services.TransactionService;
import com.example.hodlhub.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> add(@RequestBody @Valid RequestTransactionDTO requestTransactionDTO,
                                               BindingResult bindingResult,
                                               @AuthenticationPrincipal HolderDetails holderDetails) {
        if (bindingResult.hasErrors()) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            ApiResponse<Void> response = new ApiResponse<>(
                    status,
                    bindingResult,
                    "/transaction"
            );
            return new ResponseEntity<>(response, status);
        }
        Transaction transaction = transactionService.mapToEntity(requestTransactionDTO);
        transactionService.save(transaction, holderDetails.getUsername());

        HttpStatus status = HttpStatus.CREATED;
        ApiResponse<Void> response = new ApiResponse<>(
                status,
                "Transaction added successfully",
                "/transaction"
        );

        return new ResponseEntity<>(response, status);
    }
}
