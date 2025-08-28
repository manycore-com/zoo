package org.virtual;

import java.lang.reflect.Method;

public class VirtualInvoking {

    static class A {
        void m() { System.out.println("A.m"); }

        // For overload-resolution “weirdness”
        void n(Object o) { System.out.println("A.n(Object)"); }
        void n(String s) { System.out.println("A.n(String)"); }
    }

    static class B extends A {
        @Override void m() { System.out.println("B.m"); }
    }

    static class Helper {
        static void callIt(A a) { a.m(); }
    }

    void scenario1_onlyOneCallsite() {
        System.out.println("[OnlyOnce] start");
        new A().m();
        System.out.println("[OnlyOnce] end");
    }

    void scenario2_twoCallsites() {
        System.out.println("[TwoCalls] start");
        A a = new A();
        a.m();
        a.m();
        System.out.println("[TwoCalls] end");
    }
    void scenario3_passedAsParameter() {
        System.out.println("[ParamSite] start");
        Helper.callIt(new B());
        System.out.println("[ParamSite] end");
    }

    void scenario4_polymorphicResolution() {
        System.out.println("[Polymorphic] start");
        A a = new B();
        a.m();
        System.out.println("[Polymorphic] end");
    }

    void scenario5_reflectionVirtualDispatch() throws Exception {
        System.out.println("[Reflection] start");
        A a = new B();
        Method m = A.class.getDeclaredMethod("m");
        m.invoke(a);
        System.out.println("[Reflection] end");
    }

    void scenario6_overloadResolutionWithNull() {
        System.out.println("[Overload(null)] start");
        A a = new A();
        a.n(null);
        a.n((Object) null);
        System.out.println("[Overload(null)] end");
    }

    void scenario7_nullReceiverNPE() {
        System.out.println("[NullReceiver] start");
        A a = null;
        try {
            a.m();
        } catch (NullPointerException npe) {
            System.out.println("Caught NPE at callsite of invokevirtual (null receiver)");
        }
        System.out.println("[NullReceiver] end");
    }
    static class X { Y b() { System.out.println("X.b"); return new Y(); } }
    static class Y { A c() { System.out.println("Y.c"); return new B(); } }
    void scenario8_chainedReceivers() {
        System.out.println("[Chained] start");
        X x = new X();
        x.b().c().m();
        System.out.println("[Chained] end");
    }

    static class Node {
        Node m1() {
            System.out.println("Node.m1() no args");
            return this;
        }
        Node m1(Node other) {
            System.out.println("Node.m1(Node) with arg=" + other);
            return this;
        }
    }
    void scenario9_nestedArgumentReceiver() {
        Node n = new Node();
        n.m1(n.m1());
    }

    public static void main(String[] args) throws Exception {
        VirtualInvoking demo = new VirtualInvoking();

        demo.scenario1_onlyOneCallsite();
        demo.scenario2_twoCallsites();
        demo.scenario3_passedAsParameter();
        demo.scenario4_polymorphicResolution();
        demo.scenario5_reflectionVirtualDispatch();
        demo.scenario6_overloadResolutionWithNull();
        demo.scenario7_nullReceiverNPE();
        demo.scenario8_chainedReceivers();
        demo.scenario9_nestedArgumentReceiver();
    }


}
