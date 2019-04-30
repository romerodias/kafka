package br.com.rdtecnologia.kafka.producer;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class Metrica {

    //    Data/Hora	date
    private Date data;

    //    CPF usuário	CPF
    private String cpf;

    //    Mac da balança	MAC
    private String macBalanca;

    //    Peso	kg
    private Double kgPeso;

    //    Taxa de músculo	%
    private Double percentualTaxaMusculo;

    //    Teor de água	%
    private Double percentualTeorAgua;

    //    Massa óssea	kg
    private Double kgMassaOssea;

    //    Taxa Metabólica Basal(TMB)	kcal
    private Double kcalTaxaMetabolicaBasal;

    //    Percentagem de Proteína	%
    private Double percentualProteina;

    //    Idade corporal	Anos
    private Integer idadeCorporal;

    //    Índice de Gordura Visceral	escala 
    private Integer idadeGorduraVisceral;

    //    Taxa de gordura subcutânea	%
    private Double taxaGorduraSubcutanea;

    //    Gordura Corporal	kg
    private Double kgGorduraCoporal;

    //    Índice de massa corporal(IMC)	escala
    private Double indiceMassaCorporal;

    //  Taxa de gordura corporal(TGC)	%
    private Double percentualTaxaGorduraCoroporal;


    @Builder
    public Metrica(Date data, String cpf, String macBalanca, Double kgPeso,
        Double percentualTaxaMusculo, Double percentualTeorAgua, Double kgMassaOssea,
        Double kcalTaxaMetabolicaBasal, Double percentualProteina, Integer idadeCorporal,
        Integer idadeGorduraVisceral, Double taxaGorduraSubcutanea, Double kgGorduraCoporal,
        Double indiceMassaCorporal, Double percentualTaxaGorduraCoroporal) {
        this.data = data;
        this.cpf = cpf;
        this.macBalanca = macBalanca;
        this.kgPeso = kgPeso;
        this.percentualTaxaMusculo = percentualTaxaMusculo;
        this.percentualTeorAgua = percentualTeorAgua;
        this.kgMassaOssea = kgMassaOssea;
        this.kcalTaxaMetabolicaBasal = kcalTaxaMetabolicaBasal;
        this.percentualProteina = percentualProteina;
        this.idadeCorporal = idadeCorporal;
        this.idadeGorduraVisceral = idadeGorduraVisceral;
        this.taxaGorduraSubcutanea = taxaGorduraSubcutanea;
        this.kgGorduraCoporal = kgGorduraCoporal;
        this.indiceMassaCorporal = indiceMassaCorporal;
        this.percentualTaxaGorduraCoroporal = percentualTaxaGorduraCoroporal;
    }
}
