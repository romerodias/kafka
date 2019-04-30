package br.com.ezvida.sesi.consumermetrica.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sun.util.resources.LocaleData;

@Document(collection = "metricas") @Data public class Metrica {
    @Id private ObjectId _id;

    //    Data/Hora	date
    private LocaleData data;

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

    // ObjectId needs to be converted to string
    public String get_id() {
        return _id.toHexString();
    }
}
