package edu.illinois.cs.cogcomp.saul;

import edu.illinois.cs.cogcomp.lbjava.infer.GurobiHook;
import edu.illinois.cs.cogcomp.lbjava.infer.ILPInference;
import edu.illinois.cs.cogcomp.lbjava.infer.ParameterizedConstraint;
import edu.illinois.cs.cogcomp.lbjava.learn.IdentityNormalizer;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.learn.Normalizer;

/**
 * Created by haowu on 11/16/14.
 * Email : haowu@haowu.me
 */
public abstract class JointTemplate<T> extends ILPInference {


//    private Class<T> tType;
//    Class<T> getType(){return tType;}

    public JointTemplate() {
//      System.out.println(" Empty Constructor ");
    }
    public JointTemplate(T head)
    {

        super(head, new GurobiHook());
//        verbosity = 2;
//        System.out.println(" NONEmpty Constructor " + head);

//        System.out.println();


        // TODO: Uncomment this line after find the correct type for getSubjectToInstance.
        constraint = this.getSubjectToInstance().makeConstraint(head);
//        tType = (Class<T>) head.getClass();

    }


//    public JointTemplate<T> init(T head){
//
//        System.out.println("Setting head to " + head);
//        this.head = head;
//        variables = new LinkedHashMap();
//        solver = new GurobiHook();
//        verbosity = VERBOSITY_NONE;
//        ID = nextID++;
//
//        constraint = this.getSubjectToInstance().makeConstraint(head);
////        tType = (Class<T>) head.getClass();
//        return this;
//    }


    public String getHeadType() { return "T"; }

    public String[] getHeadFinderTypes()
    {
        return new String[]{ null, null };
    }

    public Normalizer getNormalizer(Learner c)
    {
        return new IdentityNormalizer();
    }

    public abstract ParameterizedConstraint getSubjectToInstance();
//    {
//        new JointER$subjectto()
//    }

}
