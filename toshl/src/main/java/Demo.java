public class Demo {

    private static RpcFrameworkEntrancePoint framework;

    //-------rpc framework(entrance point)----------

    private static class RpcFrameworkEntrancePoint {
        // assumed that framework find that incoming data indicates itself as b's method
        private AppBFacade b;

        Object nioGetReadEvent() {
            // wait read event ready and read incoming data. now assumed that incoming data is 
            // AppBFacade.paydecisionConsult's info
            // ... 
            return null;
        }

        void nioPutWriteEvent(Object methodReturn) {
            // put into nio framework and let it send this to app A's process
            //...
            return;
        }

        void mainLoop() {
            for (;;) {
                Object request = nioGetReadEvent();
                // convert to AppB request class and find AppBFacade class.method by reflection
                // ... 

                // framework can check if AppBFacade has "@EntranceAsync" annotation, now assumed it's true
                boolean bAnnotatedAsEntranceAsync = true;
                if (bAnnotatedAsEntranceAsync) { // non-entrance-async logic
                    b.paydecisionConsult((String) request);
                } else {// traditional non-entrance-async logic
                    AppBResult rst = b.paydecisionConsult((String) request);
                    nioPutWriteEvent(rst);
                }
            }
        }

    }

    public static void main(String[] args) {
        framework.mainLoop();
    }

    //--------app B---------------
    private static interface AppBFacade {
        AppBResult paydecisionConsult(String paramsXXX);
    }

    private static class AppBResult {
    }

    private static class AppBFacadeImpl implements AppBFacade {

        @Override
        public AppBResult paydecisionConsult(String paramsXXX) {
            c.vouchercoreConsult("xxx");
            return null;
        }

        public void integrationCallback(AppCResult cRst) {
            // if cRst is success, do some check and app level post-process, then decide that paydecisionConsult is successfully processed
            AppBResult bResult = new AppBResult();
            // set bResult success and fill some other app level fields
            // ...
            framework.nioPutWriteEvent(bResult);
        }

    }

    // outgoing async framework(sofa already have)
    private static class AppBIntegrationCallback4C {
        private AppBFacadeImpl integrationCallback;

        public void callback(AppCResult cRst) {
            integrationCallback.integrationCallback(cRst);
        }
    }

    private static AppCFacade c;

    //-----------app C-------------
    private static interface AppCFacade {
        AppCResult vouchercoreConsult(String paramXXX);
    }

    private static class AppCResult {
    }
}
