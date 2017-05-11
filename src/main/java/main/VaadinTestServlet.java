package main;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import com.vaadin.server.VaadinServlet;

@WebServlet(
		asyncSupported=true,
		urlPatterns={"/*","/VAADIN/*"},
		initParams={
				@WebInitParam(name="ui", value="main.RoleTagger")
		})
public class VaadinTestServlet extends VaadinServlet {
	private static final long serialVersionUID = 6517660677982027458L; 
}
