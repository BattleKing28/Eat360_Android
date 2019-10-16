package th.co.apps360.eat360;

/**
 * Created by jakkrit.p on 2/09/2015.
 */
public class Callback {

    public interface CallbackJsonResult {

        void callbackJsonResult(String keyword,String jsonResult,String possibleKey);

    }

    public interface CallbackFragmentDetailResult {

        void callbackJsonResult(String jsonResult);

    }

}
