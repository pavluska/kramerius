package cz.incad.kramerius.rights.server;


import org.aplikator.server.descriptor.Arrangement;
import org.aplikator.server.descriptor.Entity;
import org.aplikator.server.descriptor.Form;
import org.aplikator.server.descriptor.HorizontalPanel;
import org.aplikator.server.descriptor.QueryGenerator;
import org.aplikator.server.descriptor.TextArea;
import org.aplikator.server.descriptor.TextField;
import org.aplikator.server.descriptor.VerticalPanel;

public class RightsCriteriumParamArrangement extends Arrangement {
	
	private Structure structure;
	
	public RightsCriteriumParamArrangement(Entity entity, Structure struct) {
		super(entity);
		this.structure = struct;
		setReadableName(struct.criteriumParam.getName());
		setSortProperty(struct.criteriumParam.SHORT_DESC);	
		addProperty(struct.criteriumParam.SHORT_DESC);
		addProperty(struct.criteriumParam.VALS);

		
		queryGenerator = new QueryGenerator.Empty();

		form = createRightCriteriumParamForm();
	}

	
	private Form createRightCriteriumParamForm() {
		Form form = new Form();

		TextField shortDesc = new TextField(structure.criteriumParam.SHORT_DESC);
		shortDesc.setWidth("100%");

		TextArea longDesc = new TextArea(structure.criteriumParam.LONG_DESC);
		longDesc.setWidth("100%");

		TextArea values = new TextArea(structure.criteriumParam.VALS);
		values.setWidth("100%");

		form.setLayout(new VerticalPanel()
				.addChild(shortDesc)
				.addChild(longDesc)
				.addChild(values)
		);
		return form;
	}

}