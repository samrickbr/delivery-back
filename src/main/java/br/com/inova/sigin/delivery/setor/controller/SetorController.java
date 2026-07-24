package br.com.inova.sigin.delivery.setor.controller;

import br.com.inova.sigin.delivery.setor.entity.Setor;
import br.com.inova.sigin.delivery.setor.repository.SetorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setores")
@RequiredArgsConstructor
public class SetorController {

    private final SetorRepository repository;

    @GetMapping
    public List<Setor> listar() {
        return repository.findAll();
    }
}