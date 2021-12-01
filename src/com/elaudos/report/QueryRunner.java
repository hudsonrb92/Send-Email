package com.elaudos.report;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class QueryRunner {

	
    private Calendar calendar = Calendar.getInstance();
    private String baseName;
    private String mes = String.format("%02d", calendar.get(Calendar.MONTH)+1); // pega mês contando janeiro como 0;
    private String ano = String.format("%04d", (calendar.get(Calendar.YEAR)));
    private String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)); // pega dia e já coloca no formato que queremos
    private String sheetName;
    private String query = "SELECT \n" +
    		"led.identificador AS \"Identificador do laudo\",\n" +
    		"ed.data_hora_inclusao AS \"Data de envio do exame\",\n" +
    		"led.data_hora_emissao AS \"Data Hora Emissão\",\n" +
    		"ed.patientname AS \"Nome Paciente\",\n" +
    		"ed.identificador AS \"Identificador do exame\",\n" +
    		"CASE \n" +
    		"\tWHEN ed.identificador IN (pe.identificador_exame) THEN p2.descricao \n" +
    		"\tELSE ed.studydescription \n" +
    		"END AS \"Descrição do exame\",\n" +
    		"es.nome_fantasia AS \"Estabelecimento de saúde\",\n" +
    		"ed.modalitiesinstudy AS \"Modalidade de exame\",\n" +
    		"p.nome AS \"Medico executor\",\n" +
    		"CASE \n" +
    		"\tWHEN ed.modalitiesinstudy IN ('MG') AND ed.numero_exames_ris = 4 THEN 1 -- corrige quando FOR mamografia e o número FOR 4\n" +
    		"\tWHEN ed.identificador IN (pe.identificador_exame) THEN 1\n" +
    		"\tWHEN (ed.numero_exames_ris IS NULL AND led.numero_exames_relacionados IS null) THEN 1\n" +
    		"\tWHEN ed.numero_exames_ris IS NULL THEN led.numero_exames_relacionados \n" +
    		"\tELSE ed.numero_exames_ris \n" +
    		"END AS \"Laudos\"\n" +
    		"FROM radius_taas.laudo_estudo_dicom led \n" +
    		"LEFT JOIN radius_taas.estudo_dicom ed ON led.identificador_estudo_dicom = ed.identificador \n" +
    		"LEFT JOIN profissional_saude ps ON led.identificador_profissional_saude = ps.identificador \n" +
    		"LEFT JOIN pessoa p ON ps.identificador_pessoa = p.identificador \n" +
    		"LEFT JOIN estabelecimento_saude es ON ed.identificador_estabelecimento_saude = es.identificador \n" +
    		"LEFT JOIN procedimento_exame pe ON ed.identificador = pe.identificador_exame \n" +
    		"LEFT JOIN procedimentos p2 ON pe.identificador_procedimento = p2.identificador_procedimento \n" +
    		"WHERE ed.identificador_estabelecimento_saude IN %s -- dos seguintes estabelecimentos\n" +
    		"AND to_char(led.data_hora_emissao, 'MMYYYY') IN ('%s') -- com DATA de emissão NO mes \n" +
    		"AND ed.situacao_laudo IN ('G', 'S')  -- entregues ou publicados\n" +
    		"AND ed.situacao IN ('V')  -- exames válidos";
    private Statement statement;

    public QueryRunner(Statement statement, String baseName) {
		this.statement = statement;
		this.baseName = baseName;
		this.sheetName = String.format(this.baseName+"%s-%s-%s.csv", this.day, this.mes, this.ano);
	}

    private String buscaEstabelecimenos() throws SQLException {
    	ResultSet resultSet = this.statement.executeQuery("select DISTINCT \n" +
    			"identificador_estabelecimento_saude \n" +
    			"from public.perfil_usuario_estabelecimento_saude pues\n" +
    			"LEFT JOIN public.usuario u ON pues.identificador_usuario = u.identificador \n" +
    			"WHERE u.login LIKE ('itms')");
    	ArrayList<Integer> estabs = new ArrayList<Integer>();
    	
    	while(resultSet.next())
    	{
    		estabs.add(resultSet.getInt(1));
    	}
    	
    	String parametroBuscaEstabelecimentos = (estabs.toString().replace('[', '(').replace(']', ')'));
    	return parametroBuscaEstabelecimentos;
    }
    
    public String geraRelatorio(String dirName) throws SQLException, IOException {
    	String formattedQuery = String.format(this.query, this.buscaEstabelecimenos(), this.mes+this.ano);
    	ResultSet relatorio = this.statement.executeQuery(formattedQuery);
    	CSVWriter writer = new CSVWriter(new FileWriter(dirName+this.sheetName));
    	writer.writeAll(relatorio,true);
    	writer.close();
    	System.out.println(relatorio.getFetchSize());
    	return dirName+this.sheetName;
    }
}
