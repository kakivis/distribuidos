
package viagem;

import java.util.ArrayList;
import java.util.Date;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * Andréia S Koda
 * Mateus O Santos
 */

@WebService(serviceName = "WebServiceSD")
public class WebServiceSD {
    // Lista contendo serviços de hospedagem disponíveis
    ArrayList<Hospedagem> hospedagem;
    // Lista contendo serviços de passagem disponíveis
    ArrayList<Passagem> passagem;
    // Lista contendo informações sobre os que já compraram passagem
    ArrayList<CompradoresPassagem> compradoresPassagem;
    // Lista contendo informações sobre os que já compraram hospedagem
    ArrayList<CompradoresHospedagem> compradoresHospedagem;
    
    // booleans para impedir que dois clientes acessem o servidor ao mesmo tempo para compra
    boolean podeComprarPassagem, podeComprarHospedagem;
    
    /* Inicialização do serviço: inicializando listas */
    public WebServiceSD() {
        hospedagem = new ArrayList<>();
        passagem = new ArrayList<>();
        compradoresPassagem = new ArrayList<>();
        compradoresHospedagem = new ArrayList<>();
        podeComprarPassagem = true;
        podeComprarHospedagem = true;
        
        hospedagem.add(new Hospedagem("Aracaju", new Date(2018, 7, 21), 1));
        hospedagem.add(new Hospedagem("Aracaju", new Date(2018, 7, 22), 3));
        hospedagem.add(new Hospedagem("Aracaju", new Date(2018, 7, 23), 3));
        hospedagem.add(new Hospedagem("Aracaju", new Date(2018, 7, 24), 2));
        hospedagem.add(new Hospedagem("Aracaju", new Date(2018, 7, 25), 3));
        
        hospedagem.add(new Hospedagem("Curitiba", new Date(2018, 7, 22), 3));
        hospedagem.add(new Hospedagem("Curitiba", new Date(2018, 7, 23), 3));
        hospedagem.add(new Hospedagem("Curitiba", new Date(2018, 7, 24), 3));
        hospedagem.add(new Hospedagem("Curitiba", new Date(2018, 7, 25), 3));
        hospedagem.add(new Hospedagem("Curitiba", new Date(2018, 7, 26), 3));
        
        hospedagem.add(new Hospedagem("Berlim", new Date(2018, 3, 11), 3));
        hospedagem.add(new Hospedagem("Berlim", new Date(2018, 3, 12), 3));
        hospedagem.add(new Hospedagem("Berlim", new Date(2018, 3, 13), 3));
        hospedagem.add(new Hospedagem("Berlim", new Date(2018, 3, 14), 3));
        hospedagem.add(new Hospedagem("Berlim", new Date(2018, 3, 15), 3));
        
        passagem.add(new Passagem("Curitiba", "Aracaju", new Date(2018, 7, 21), 5));
        passagem.add(new Passagem("Curitiba", "Aracaju", new Date(2018, 7, 22), 2));
        passagem.add(new Passagem("Curitiba", "Aracaju", new Date(2018, 7, 23), 5));
        passagem.add(new Passagem("Aracaju", "Curitiba", new Date(2018, 7, 23), 5));
        passagem.add(new Passagem("Aracaju", "Curitiba", new Date(2018, 7, 24), 5));
        passagem.add(new Passagem("Aracaju", "Curitiba", new Date(2018, 7, 25), 5));
        passagem.add(new Passagem("Curitiba", "Aracaju", new Date(2018, 7, 25), 5));
        passagem.add(new Passagem("Curitiba", "Aracaju", new Date(2018, 7, 26), 2));
        passagem.add(new Passagem("Curitiba", "Aracaju", new Date(2018, 7, 27), 5));
        
        passagem.add(new Passagem("Curitiba", "Berlim", new Date(2018, 3, 11), 5));
        passagem.add(new Passagem("Curitiba", "Berlim", new Date(2018, 3, 12), 1));
        passagem.add(new Passagem("Curitiba", "Berlim", new Date(2018, 3, 13), 7));
        passagem.add(new Passagem("Berlim", "Curitiba", new Date(2018, 3, 13), 1));
        passagem.add(new Passagem("Berlim", "Curitiba", new Date(2018, 3, 14), 8));
        passagem.add(new Passagem("Berlim", "Curitiba", new Date(2018, 3, 15), 6));
        passagem.add(new Passagem("Curitiba", "Berlim", new Date(2018, 3, 15), 5));
        passagem.add(new Passagem("Curitiba", "Berlim", new Date(2018, 3, 16), 1));
        passagem.add(new Passagem("Curitiba", "Berlim", new Date(2018, 3, 17), 7));
       
    }

    // Método para consulta de passagens
    @WebMethod(operationName = "consultaPassagens")
    public String consultaPassagens(@WebParam(name = "servico") String servico,
            @WebParam(name = "origem") String origem,
            @WebParam(name = "destino") String destino,
            @WebParam(name = "dataIda") String dataIda,
            @WebParam(name = "dataVolta") String dataVolta) {
        // Transformando as datas de strings dd/MM/AA para objeto (Date)
        String[] aux = dataIda.split("/");
        Date ida = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                Integer.parseInt(aux[0]));
        Date volta = new Date();
        if(dataVolta != null) {
            aux = dataVolta.split("/");
            volta = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                    Integer.parseInt(aux[0]));
        }
        
        // servico = 'Ida' ou 'ida e volta'
        String resultadoConsulta = "Passagens disponíveis de " + origem + " para " + destino + ":\r\n";
        boolean achou = false;
        // Procurando as passagens disponíveis que atendem os requisitos
        for(Passagem p : passagem) {
            if(p.origem.compareTo(origem) == 0 && p.destino.compareTo(destino) == 0 &&
                    p.dataIda.equals(ida)) {
                resultadoConsulta += p.toString() + "\r\n";
                achou = true;
            }
        }
        
        if(!achou)
            resultadoConsulta += "Nenhum resultado encontrado.";
        
        resultadoConsulta += "\r\n";
        
        // Se for ida e volta, realizar a busca da passagem de volta
        if(servico.toLowerCase().compareTo("ida e volta") == 0 && dataVolta != null) {
            achou = false;
            for(Passagem p : passagem) {
                if(p.origem.compareTo(destino) == 0 && p.destino.compareTo(origem) == 0 &&
                        p.dataIda.equals(volta)) {
                    resultadoConsulta += p.toString() + "\r\n";
                    achou = true;
                }
            }
            
            if(!achou)
                resultadoConsulta += "Nenhum resultado encontrado.\r\n";
        }
        
        // Retorna resultado da consulta já formatado para exibição
        return resultadoConsulta;
    }
    
    /* Método para consulta de hospedagem */
    @WebMethod(operationName = "consultaHospedagem")
    public String consultaHospedagem(@WebParam(name = "origem") String origem,
            @WebParam(name = "dataChegada") String dataChegada,
            @WebParam(name = "dataSaida") String dataSaida) {
        // Transformando as datas de strings dd/MM/AA para objeto (Date)
        String[] aux = dataChegada.split("/");
        Date chegada = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                Integer.parseInt(aux[0]));
        aux = dataSaida.split("/");
        Date saida = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                Integer.parseInt(aux[0]));
        
        String resultadoConsulta = "Hospedagens encontradas entre " +
                dataChegada + " e " + dataSaida + " em " + origem + ":\r\n";
        Date dateAux = new Date(chegada.getYear(), chegada.getMonth(), chegada.getDate());

        // Procurando todas as hospedagens para todos os dias entre data de entrada e data de saída
        while(dateAux.before(saida) || dateAux.equals(saida)) {
            boolean achou = false;
            for(Hospedagem h : hospedagem) {
                if(h.data.equals(dateAux) && h.local.toLowerCase().compareTo(origem.toLowerCase()) == 0) {
                    resultadoConsulta += h.toString() + "\r\n";
                    achou = true;
                    break;
                }
            }
            
            dateAux.setDate(dateAux.getDate()+1);
            
            if(!achou)
                return "Não foi possível encontrar hospedagem para todos os dias";
        }

        
        
        resultadoConsulta += "\r\n";
        
        // Retorna resultado da consulta já formatado
        return resultadoConsulta;
    }
    
    /* Método para compra de passagens */
    @WebMethod(operationName = "compraPassagens")
    public String compraPassagens(@WebParam(name = "servico") String servico,
            @WebParam(name = "origem") String origem,
            @WebParam(name = "destino") String destino,
            @WebParam(name = "dataIda") String dataIda,
            @WebParam(name = "dataVolta") String dataVolta,
            @WebParam(name = "numPessoas") int numPessoas,
            @WebParam(name = "numeroCartao") String numeroCartao,
            @WebParam(name = "vencimentoCartao") String vencimentoCartao,
            @WebParam(name = "nomeCartao") String nomeCartao,
            @WebParam(name = "codigoSegurancaCartao") String codigoSegurancaCartao,
            @WebParam(name = "parcelado") int parcelado) {
        // Transformando as datas de strings dd/MM/AA para objeto (Date)
        String[] aux = dataIda.split("/");
        Date ida = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                Integer.parseInt(aux[0]));
        Date volta = new Date();
        if(dataVolta != null) {
            aux = dataVolta.split("/");
            volta = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                    Integer.parseInt(aux[0]));
        }
        
        // Impedindo que dois clientes comprem passagem ao mesmo tempo
        while(!podeComprarPassagem);
        podeComprarPassagem = false;
        
        // Se não existe a passagem de ida, retorna
        Passagem passagemIda = null;
        for(Passagem p : passagem) {
            if(p.origem.toLowerCase().compareTo(origem.toLowerCase()) == 0 &&
                    p.destino.toLowerCase().compareTo(destino.toLowerCase()) == 0 &&
                    p.dataIda.equals(ida)) {
                passagemIda = p;
            }
        }
        
        if(passagemIda == null) return "Não foi possível encontrar a passagem de ida";
        
        // Se não existe a passagem de volta, retorna
        Passagem passagemVolta = null;
        if(servico.toLowerCase().compareTo("ida e volta") == 0) {
            for(Passagem p : passagem) {
                if(p.origem.compareTo(destino) == 0 && p.destino.compareTo(origem) == 0 &&
                        p.dataIda.equals(volta)) {
                    passagemVolta = p;
                }
            }

            if(passagemVolta == null) return "Não foi possível encontrar a passagem de volta";
            
            // Se não tem vagas suficientes na volta, retorna
            if(passagemVolta.numAssentos < numPessoas) return "Não há vagas suficientes da volta";
        }
        
        // Se não tem vagas suficientes na ida, retorna
        if(passagemIda.numAssentos < numPessoas) return "Não há vagas suficientes na ida";
        
        // Compra da passagem de ida
        passagemIda.compraPassagem(numPessoas);
        
        // Se não há mais vaga, remove da lista
        if(passagemIda.numAssentos == 0)
            passagem.remove(passagemIda);
        
        if(passagemVolta != null) {
            // Compra da passagem de volta
            passagemVolta.compraPassagem(numPessoas);
            // Se não há mais vagas, remove da lista
            if(passagemVolta.numAssentos == 0)
                passagem.remove(passagemVolta);
        }
        
        DadosCartao dadosCartao = new DadosCartao(numeroCartao, vencimentoCartao,
                nomeCartao, codigoSegurancaCartao);
        
        boolean b = parcelado == 0;
        
        // Salva comprador
        CompradoresPassagem comprador = new CompradoresPassagem(servico, ida, volta,
            origem, destino, numPessoas, dadosCartao, b);
        compradoresPassagem.add(comprador);
        
        // Permite compra de outro cliente
        podeComprarPassagem = true;
        return "Compra efetuada com sucesso";
    }
    
    /* Método para compra de hospedagem */
    @WebMethod(operationName = "compraHospedagem")
    public String compraHospedagem(@WebParam(name = "origem") String origem,
            @WebParam(name = "dataEntrada") String dataEntrada,
            @WebParam(name = "dataSaida") String dataSaida,
            @WebParam(name = "numQuartos") int numQuartos,
            @WebParam(name = "numPessoas") int numPessoas,
            @WebParam(name = "numeroCartao") String numeroCartao,
            @WebParam(name = "vencimentoCartao") String vencimentoCartao,
            @WebParam(name = "nomeCartao") String nomeCartao,
            @WebParam(name = "codigoSegurancaCartao") String codigoSegurancaCartao,
            @WebParam(name = "parcelado") int parcelado) {

        // Transformando as datas de strings dd/MM/AA para objeto (Date)
        String[] aux = dataEntrada.split("/");
        Date entrada = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                Integer.parseInt(aux[0]));

        aux = dataSaida.split("/");
        Date saida = new Date(Integer.parseInt(aux[2]), Integer.parseInt(aux[1])-1,
                    Integer.parseInt(aux[0]));

        // Impedindo que dois cliente comprem passagem ao mesmo tempo
        while(!podeComprarHospedagem);
        podeComprarHospedagem = false;
        
        // Procurando todas as hospedagens para todos os dias entre entrada e saída
        Date dateAux = new Date(entrada.getYear(), entrada.getMonth(), entrada.getDate());
        ArrayList<Hospedagem> compras = new ArrayList<>();
        while(dateAux.before(saida) || dateAux.equals(saida)) {
            boolean achou = false;
            for(Hospedagem h : hospedagem) {
                if(h.data.equals(dateAux) && h.numQuartos >= numQuartos && 
                        h.local.toLowerCase().compareTo(origem.toLowerCase()) == 0) {
                    compras.add(h);
                    achou = true;
                    break;
                }
            }
            
            dateAux.setDate(dateAux.getDate()+1);
            
            // Se não encontrou um dos dias, retorna
            if(!achou) return "Não foi possível encontrar hospedagem para todos os dias";
        }
        
        // Diminui número de quartos disponíveis
        for(Hospedagem h : compras) {
            h.numQuartos -= numQuartos;
            if(h.numQuartos == 0)
                hospedagem.remove(h);
        }
        
        DadosCartao dadosCartao = new DadosCartao(numeroCartao, vencimentoCartao,
                nomeCartao, codigoSegurancaCartao);
        
        boolean b = parcelado == 0;
        
        CompradoresHospedagem comprador = new CompradoresHospedagem(origem, entrada,
            saida, numQuartos, dadosCartao, b);
        // Salva os dados do comprador
        compradoresHospedagem.add(comprador);
        
        // Permite que outro cliente realize a compra
        podeComprarHospedagem = true;
        return "Compra efetuada com sucesso";
    }

}
