package com.example.calculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public CalculationResult add(@RequestParam double a, @RequestParam double b) {
        return new CalculationResult("add", a, b, service.add(a, b));
    }

    @GetMapping("/subtract")
    public CalculationResult subtract(@RequestParam double a, @RequestParam double b) {
        return new CalculationResult("subtract", a, b, service.subtract(a, b));
    }

    @GetMapping("/multiply")
    public CalculationResult multiply(@RequestParam double a, @RequestParam double b) {
        return new CalculationResult("multiply", a, b, service.multiply(a, b));
    }

    @GetMapping("/divide")
    public CalculationResult divide(@RequestParam double a, @RequestParam double b) {
        return new CalculationResult("divide", a, b, service.divide(a, b));
    }
}
