package org.crain.dme4j.engine;

import org.crain.dme4j.core.types.*;

@SuppressWarnings("unused")


public class FopAcC extends Struct {

    IntValue actorType;
    CreateTagClass actorTag;
    CreateTagClass drawTag;
    ClassRefPointer<ActorMethodClass> subMethod = ClassRefPointer.atNull(new ActorMethodClass());
    ClassRefPointer<JKRSolidHeap> heap = ClassRefPointer.atNull(new JKRSolidHeap());

    public FopAcC() {
        super();
        this.subMethod.makeEager();
        // There are more expectations from extending Struct that are not included.
    }
    @Override
    public String getName() {
        return "fop_ac_c";
    }



    public static class CreateTagClass {
    }

    public static class ActorMethodClass extends Struct {
    }

    public static class JKRSolidHeap extends Struct {
    }

    public static class DEvtInfoC {
    }

    public static class DkyTevStrC {
    }

    public static class FpcProcID {
    }

    public static class ActorPlace {
    }

    public static class CsXyz {
    }

    public static class CXyz {
    }

    public static class MtxP {
    }
}
