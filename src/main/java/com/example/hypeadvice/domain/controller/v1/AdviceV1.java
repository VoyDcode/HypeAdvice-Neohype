package com.example.hypeadvice.domain.controller.v1;

import com.example.hypeadvice.domain.entity.Advice;
import com.example.hypeadvice.domain.service.AdviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST que expoe as operacoes de gestao de conselhos.
 *
 * <p>Endpoints disponiveis:
 * <ul>
 *   <li>{@code GET /advice/v1/listar} - lista todos os conselhos cadastrados.</li>
 *   <li>{@code POST /advice/v1/salvar} - cadastra um novo conselho.</li>
 * </ul>
 *
 * <p>A documentacao OpenAPI eh gerada automaticamente pelas anotacoes
 * Swagger/Springdoc presentes nos metodos.
 */
@Tag(name = "Advice", description = "Gestao dos conselhos")
@CrossOrigin
@RestController
@RequestMapping("/advice/v1")
public class AdviceV1 {

    private final AdviceService adviceService;

    public AdviceV1(AdviceService adviceService) {
        this.adviceService = adviceService;
    }

    @GetMapping("/listar")
    @Operation(
            summary = "Listar todos os conselhos cadastrados.",
            description = "Metodo utilizado para listar todos os conselhos cadastrados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso.", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Advice.class)))})
    })
    public ResponseEntity<List<Advice>> listar() {
        List<Advice> all = adviceService.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PostMapping("/salvar")
    @Operation(
            summary = "Cadastrar um novo conselho.",
            description = "Metodo utilizado para cadastrar um novo conselho no sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conselho cadastrado com sucesso.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Advice.class))})
    })
    public ResponseEntity<Advice> salvar(@RequestBody Advice advice) {
        Advice saved = adviceService.save(advice);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
