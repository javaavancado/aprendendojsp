package br.com.aocp.filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import br.com.aocp.connection.SingletonConnetion;

@WebFilter(filterName = "conexaoFilter", servletNames = { "ClientePessoaContoller" })
public class FilterConnection implements Filter {

	private static Connection connection = null;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			request.setCharacterEncoding("UTF-8");
			connection = SingletonConnetion.getConnection();
			chain.doFilter(request, response);
			connection.commit();
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.printStackTrace();
				throw new RuntimeException(
						"Erro em rollback com a base de dados, commit não foi realizado."
								+ e);
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		connection = new SingletonConnetion().getConnection();
	}
}
