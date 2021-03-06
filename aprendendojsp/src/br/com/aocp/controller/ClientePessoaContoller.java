package br.com.aocp.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.tomcat.util.codec.binary.Base64;

import br.com.aocp.dao.ClienteDao;
import br.com.aocp.entidade.ClientePessoaFisica;
import br.com.aocp.repository.RepositoryCliente;

public class ClientePessoaContoller extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private RepositoryCliente repositoryCliente;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	public ClientePessoaContoller() {
		super();
		repositoryCliente = new ClienteDao();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
		if (req.getParameter("action") != null) {
			if (req.getParameter("clienteId") != null) {
				
				String action = req.getParameter("action");
				String clienteId = req.getParameter("clienteId");

				if (action.equals("delete")) {// se for pra deletar
					repositoryCliente.deleta(clienteId);
					RequestDispatcher view = req.getRequestDispatcher("/listAll.jsp");
					req.setAttribute("clientes",repositoryCliente.consultaTodos());
					view.forward(req, resp);
				}
				if (action.equals("edit")) {// se for pra editar
					ClientePessoaFisica clientePessoaFisica = repositoryCliente.consulta(clienteId);
					RequestDispatcher view = req.getRequestDispatcher("/index.jsp");
					req.setAttribute("cliente", clientePessoaFisica);
					view.forward(req, resp);
				}
				
			}
		}
		} catch (Exception e) {
			RequestDispatcher view = req.getRequestDispatcher("/index.jsp");
			req.setAttribute("erro", e.getMessage());
			view.forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String action = req.getParameter("action");
			String imgBase64 = req.getParameter("base64");
			
			if (imgBase64 != null && !imgBase64.isEmpty()) {
				
				String valorX = req.getParameter("x");
				String valorY = req.getParameter("y");
				String valorW = req.getParameter("w");
				String valorH = req.getParameter("h");
				
				if (valorX != null && valorY != null && valorW != null && valorH != null){
					if (!valorX.isEmpty() && !valorY.isEmpty() && !valorW.isEmpty() && !valorH.isEmpty()){
						  
						    //Imagem original em Base64
						    String base64Image = imgBase64.split(",")[1];
						    
						    //Convertendo para  byte[] usando lib apache
							byte[] imageBytes = new Base64().decodeBase64(base64Image);
							
							// Transformando em BufferedImage 
							BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
							
							// Cortando as imagens passando as cordenadas
							BufferedImage subImgage = bufferedImage. 
									getSubimage(Integer.parseInt(valorX) ,  
											Integer.parseInt(valorY) , 
											Integer.parseInt(valorW) , 
											Integer.parseInt(valorH) );
							 
							 // escrevendo novamente a imagem cortada.
							 ByteArrayOutputStream baos = new ByteArrayOutputStream();
						     ImageIO.write(subImgage, "png", baos);
						     
						     // convertendo para base64 novamente
						     imgBase64 = "data:image/png;base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());
						
					}
				}
				
			}
			
			if ((action != null && !action.equalsIgnoreCase("listar"))|| action == null) {

				String idTemp = req.getParameter("idTemp");
				ClientePessoaFisica clientePessoaFisica = new ClientePessoaFisica();
				
				if (req.getParameter("id") != null && !req.getParameter("id").isEmpty() && req.getParameter("id") != null) {
					clientePessoaFisica.setId(Long.parseLong(req.getParameter("id")));
				} else {
					clientePessoaFisica.setId(null);
				}

				clientePessoaFisica.setFoto(imgBase64);
				clientePessoaFisica.setCpf(req.getParameter("cpf").toString());
				clientePessoaFisica.setDataNacimento(simpleDateFormat.parse(req.getParameter("datanascimento")));
				clientePessoaFisica.setEndereco(req.getParameter("endereco"));
				clientePessoaFisica.setNome(req.getParameter("nome"));
				clientePessoaFisica.setNumeroLogradouro(Integer.parseInt(req.getParameter("numeroLogradouro")));
				
				clientePessoaFisica.setAtivo((req.getParameter("ativo")));
				clientePessoaFisica.setSexo(req.getParameter("sexo"));
				
				// check multiplos INICIO
					String[] experiencia = req.getParameterValues("experiencia");
					if (experiencia != null) {
						String exp = Arrays.toString(experiencia).replaceAll("\\[", "").replaceAll("\\]", "");
						clientePessoaFisica.setExperiencia(exp);
					}
				// check multiplos FIM
				
				if (action.equals("delete")) {
					repositoryCliente.deleta(clientePessoaFisica.getId());
				} else if (action.equals("save") && (idTemp == null || idTemp.isEmpty())) {
					repositoryCliente.salvar(clientePessoaFisica);
				} else if (action.equals("edit") || (action.equals("save") && idTemp != null && !idTemp.isEmpty())) {
					repositoryCliente.atualiza(clientePessoaFisica);
				}

				RequestDispatcher view = req.getRequestDispatcher("/listAll.jsp");
				req.setAttribute("clientes", repositoryCliente.consultaTodos());
				view.forward(req, resp);
			} else if (action != null && action.equalsIgnoreCase("listar")) {
				RequestDispatcher view = req.getRequestDispatcher("/listAll.jsp");
				req.setAttribute("clientes", repositoryCliente.consultaTodos());
				view.forward(req, resp);
			}

		} catch (Exception e) {
			RequestDispatcher view = req.getRequestDispatcher("/index.jsp");
			req.setAttribute("erro", e.getMessage());
			view.forward(req, resp);
		}
	}
}
