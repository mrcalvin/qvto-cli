package at.ac.wu.nm.qvto;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import at.ac.wu.nm.qvto.App;

public class BasicTest {

	@BeforeMethod
	@BeforeClass
	public void setUp() {
		// code that will be invoked when this test is instantiated
	}

	@Test
	public void aFastTest() {
		System.out.println("Fast test");
		//java -jar qvto-app-0.1-SNAPSHOT.jar 
		//-xform 
		//../src/test/resources/qvto4abcmodel/transforms/transformationABC.qvto 
		//-src
		//../src/test/resources/qvto4abcmodel/modelSource/MMSource.ecore 
		//../src/test/resources/qvto4abcmodel/modelSource/MSource.xmi
		//-tgt
		//../src/test/resources/qvto4abcmodel/modelSource/MMSource.ecore 
		//./src/test/resources/qvto4abcmodel/modelTarget/my.xmi


		String[] args= new String[8];
		args[0]="-xform";
		args[1]="src/test/resources/qvto4abcmodel/sample/transformationABC.qvto";
		args[2]="-src";
		args[3]="src/test/resources/qvto4abcmodel/sample/MMSource.ecore";
		args[4]="src/test/resources/qvto4abcmodel/sample/MSource.xmi";
		args[5]="-tgt";
		args[6]="src/test/resources/qvto4abcmodel/sample/MMSource.ecore";
		args[7]="src/test/resources/qvto4abcmodel/sample/Mtarget.xmi";


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

}
