import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgJsonReader;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.owlapi.FromOwl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 */
public class OBOGraphsTesterApp {

    private final File ontology_file;
    private final File testdir;

    private OBOGraphsTesterApp(File ontology_file, File testdir) throws OWLOntologyCreationException {
        this.ontology_file = ontology_file;
        this.testdir = testdir;
        run();
    }

    private void run() {
        String name = ontology_file.getName();
        File jsonOutFile = new File(testdir, name + "_1.json");
        File jsonOutFile2 = new File(testdir, name + "_2.json");

        try {
            GraphDocument gd = new LoadOntology().invoke(ontology_file);
            String jsonStr = OgJsonGenerator.render(gd);
            FileUtils.writeStringToFile(jsonOutFile, jsonStr, "utf-8");
            GraphDocument gd2 = new LoadOntology().invoke(ontology_file);
            String jsonStr2 = OgJsonGenerator.render(gd2);
            FileUtils.writeStringToFile(jsonOutFile2, jsonStr2, "utf-8");

            if (FileUtils.contentEquals(jsonOutFile2, jsonOutFile)) {
                System.out.println(ontology_file.getName() + " deterministic output");
            } else {
                System.out.println(ontology_file.getName() + " output NOT deterministic!");
                System.out.println("diff " + jsonOutFile + " " + jsonOutFile2);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ontology_file.getName() + " did NOT roundtrip!");
            System.out.println("diff " + jsonOutFile + " " + jsonOutFile2);
        }

    }

    private void log(Object o) {
        System.out.println(o.toString());
    }

    public static void main(String[] args) throws OWLOntologyCreationException, IOException {



		String ontology_path = args[0];
		String testdir_path = args[1];
/*
        String ontology_path = "/Users/matentzn/ws/c-elegans-phenotype-ontology/wbphenotype.owl";
        String testdir_path = "/Users/matentzn/data";

 */

        File ontology_file = new File(ontology_path);
        File testdir_file = new File(testdir_path);
        new OBOGraphsTesterApp(ontology_file, testdir_file);
    }

    private class LoadOntology {
        public GraphDocument invoke(File ontology_file) throws IOException, OWLOntologyCreationException {
            GraphDocument gd;
            if (ontology_file.getName().endsWith(".json")) {
                gd = OgJsonReader.readFile(ontology_file);
            } else {
                OWLOntologyManager m = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = m.loadOntologyFromOntologyDocument(ontology_file);
                FromOwl fromOwl = new FromOwl();
                gd = fromOwl.generateGraphDocument(ontology);
            }
            return gd;
        }
    }
}
