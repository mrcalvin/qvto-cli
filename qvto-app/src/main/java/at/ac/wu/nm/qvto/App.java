package at.ac.wu.nm.qvto;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.qvt.oml.BasicModelExtent;
import org.eclipse.m2m.qvt.oml.ExecutionContextImpl;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.eclipse.m2m.qvt.oml.TransformationExecutor;
import org.eclipse.m2m.qvt.oml.util.Log;
import org.eclipse.m2m.qvt.oml.util.WriterLog;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class App {
    
    @Parameter
    private List<String> parameters = new ArrayList<String>();
    
    @Parameter(names = { "-src" }, description = "Pairs of source metamodel and source model", required = true, variableArity = true)
    private List<String> source = new ArrayList<String>();
    
    @Parameter(names = { "-tgt" }, description = "Pair of target metamodel and target model", arity = 2)
    private List<String> target = new ArrayList<String>();

    @Parameter(names = { "-xform", "--transformation" }, description = "QVTo transformation to be executed", required = true)
    private String transformation;

    private ResourceSet rs = new ResourceSetImpl();
    
    public static void main(String[] args) {
		
	App app = new App();
	JCommander jc = new JCommander(app);
	
	try {
	    jc.parse(args);
	    /* TODO: check whether -src value is an even list */
	} catch (ParameterException pe) {
	    System.out.println(pe.getMessage());
	    jc.usage();
	    return;
	}
	
	app.build();
	
    }

    private ModelExtent processSourcePair(String metamodel, String model) {
	Resource mmRes =
	    this.rs.getResource(URI.createFileURI(new File(metamodel).getAbsolutePath()), true);

	EObject eObject = mmRes.getContents().get(0);
	if (eObject instanceof EPackage) {
	    EPackage p = (EPackage)eObject;
	    // rs.getPackageRegistry().put(p.getNsURI(), p);
	    EPackage.Registry.INSTANCE.put(p.getNsURI(), p);
	}
	
	Resource mRes =
	    this.rs.getResource(URI.createFileURI(new File(model).getAbsolutePath()), true);

	/* TODO: validate model against metamodel */
	
	return new BasicModelExtent(mRes.getContents());
    }
    
    public void build() {
       
	this.rs.getResourceFactoryRegistry().
	    getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	
	this.rs.getResourceFactoryRegistry().
	    getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

	EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE); 
	Resource.Factory.Registry.INSTANCE.
	    getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE); 



	List<ModelExtent> modelExtents = new ArrayList<ModelExtent>();
	
	for(int i = 0; i < this.source.size(); i += 2) {
	    modelExtents.add(this.processSourcePair(this.source.get(i),this.source.get(i+1)));
	}
	
	URI txURI = URI.createFileURI(new File(this.transformation).getAbsolutePath());
	TransformationExecutor executor=new TransformationExecutor(txURI);

	/* TODO: Does the CLI input match the QVTo transformation (xform) signature? 
	   Use TransformationExecutor.loadTransformation() */ 

	/* TODO: Put a scheme in place to support different file extensions and 
	   use them to identify metamodels?! */
	

	

	/* TODO: support multiple out parameters for xform */
	
	ModelExtent output = new BasicModelExtent();
	modelExtents.add(output);
	
	ExecutionContextImpl context = new ExecutionContextImpl();
	context.setConfigProperty("keepModeling", true);
	
	OutputStreamWriter outStream = new OutputStreamWriter(System.out);
	Log log = new WriterLog(outStream);
	context.setLog(log);
	
	ExecutionDiagnostic result = executor.execute(context,
						      modelExtents.toArray(new ModelExtent[modelExtents.size()]));
	
	if(result.getSeverity() == Diagnostic.OK) {
	    // the output objects got captured in the output extent
	    UMLResource outResource = (UMLResource) rs.createResource(URI.createFileURI(new File("out.uml").getAbsolutePath()));
	    outResource.getContents().addAll(output.getContents());
	    try {
		outResource.save(null);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} else {
	    IStatus status = BasicDiagnostic.toIStatus(result);
	    StringBuilder s = new StringBuilder();
	    s.append("Failed to execute ");
	    s.append(txURI);
	    s.append(": ");
	    s.append(status.getMessage());
	    for (IStatus child : status.getChildren()) {
		s.append("\n " + child.getMessage());
	    }
	    System.out.println(s);
	}
    }
    
}
