package br.com.rdtecnologia.kafka.producer;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Trabalhador implements Serializable {

    private Long id;

    private Date dataCriacao;

    private Date dataAlteracao;

    private String cpf;

    private String nome;

    private String matricula;

    private String cracha;

    private String email;

    private String celular;

    private Date nascimento;

    private Double altura;

    private Double circunferencia;

    private String genero;


    @Builder
    public Trabalhador(Long id, Date dataCriacao, Date dataAlteracao, String cpf, String nome,
        String matricula, String cracha, String email, String celular, Date nascimento,
        Double altura, Double circunferencia, String genero) {
        this.id = id;
        this.dataCriacao = dataCriacao;
        this.dataAlteracao = dataAlteracao;
        this.cpf = cpf;
        this.nome = nome;
        this.matricula = matricula;
        this.cracha = cracha;
        this.email = email;
        this.celular = celular;
        this.nascimento = nascimento;
        this.altura = altura;
        this.circunferencia = circunferencia;
        this.genero = genero;
    }
}
