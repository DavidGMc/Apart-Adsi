package jvm.ncatz.netbour.pck_fragment.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jvm.ncatz.netbour.R;
import jvm.ncatz.netbour.pck_pojo.PoUser;

public class FrgRegister extends Fragment {

    //foto por defecto del usuario en la base de datos apart
    private static final String DEFAULT_PHOTO = "https://firebasestorage.googleapis.com/v0/b/apart-com.appspot.com/o/cerebro.jpg?alt=media&token=b6eee698-a2de-4673-b6cc-068a71e9d4d7";

    @BindView(R.id.fragFormRegisterCode) //variable codigo
    EditText fragFormRegisterCode;
    @BindView(R.id.fragFormRegisterEmail) // email
    EditText fragFormRegisterEmail;
    @BindView(R.id.fragFormRegisterName)  // nombre
    EditText fragFormRegisterName;
    @BindView(R.id.fragFormRegisterPhone)  // telefono
    EditText fragFormRegisterPhone;
    @BindView(R.id.fragFormRegisterFloor)   //piso
    EditText fragFormRegisterFloor;
    @BindView(R.id.fragFormRegisterDoor)    // numero de apto
    EditText fragFormRegisterDoor;
    @BindView(R.id.fragFormRegisterPassword) // contraseña
    EditText fragFormRegisterPin;

    @OnClick(R.id.fragFormRegisterSave)  //validar campos a guardar
    public void onViewClicked() {
        if (canClick) {
            deactivateButton();

            long currentTime = System.currentTimeMillis();
            PoUser user = new PoUser(
                    false, PoUser.GROUP_NEIGHBOUR,
                    currentTime, fragFormRegisterCode.getText().toString().toLowerCase(),
                    fragFormRegisterDoor.getText().toString(), fragFormRegisterEmail.getText().toString(),
                    fragFormRegisterFloor.getText().toString(), fragFormRegisterName.getText().toString(),
                    fragFormRegisterPhone.getText().toString(),
                    DEFAULT_PHOTO
            );
            validateUser(user, fragFormRegisterPin.getText().toString());
        }
    }

    private IFrgRegister callback;

    private boolean canClick;

    public interface IFrgRegister {

        void userCreated(boolean successful, PoUser us);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (IFrgRegister) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        canClick = true;
    }

    @Nullable
    @Override // mostrar el xml o diseño del registro
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frg_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private void activateButton() {
        canClick = true;
    }

    //metodo firebase para crear el ususrio
    private void createUser(final PoUser user, String pass) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (callback != null) {
                            callback.userCreated(task.isSuccessful(), user);
                        }
                    }
                });
    }

    private void deactivateButton() {
        canClick = false;
    }
   //validar todos los campos correctos del usuario segun la normatividad
    private void validateUser(PoUser user, String pass) {
        boolean error = false;

        if (TextUtils.equals("", user.getCommunity())) {
            error = true;
            fragFormRegisterCode.setError(getString(R.string.ERROR_EMPTY));
        }
        if (TextUtils.equals("", user.getEmail())) {
            error = true;
            fragFormRegisterEmail.setError(getString(R.string.ERROR_EMPTY));
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
            error = true;
            fragFormRegisterEmail.setError(getString(R.string.ERROR_FORMAT));
        }
        if (TextUtils.equals("", user.getName())) {
            error = true;
            fragFormRegisterName.setError(getString(R.string.ERROR_EMPTY));
        } else if (user.getName().length() < 3) {
            error = true;
            fragFormRegisterName.setError(getString(R.string.ERROR_SHORT_3));
        } else if (user.getName().length() > 36) {
            error = true;
            fragFormRegisterName.setError(getString(R.string.ERROR_LONG_36));
        }
        if (TextUtils.equals("", user.getPhone())) {
            error = true;
            fragFormRegisterPhone.setError(getString(R.string.ERROR_EMPTY));
        } else if (user.getPhone().length() < 9) {
            error = true;
            fragFormRegisterPhone.setError(getString(R.string.ERROR_SHORT_9));
        } else if (user.getPhone().length() > 9) {
            error = true;
            fragFormRegisterPhone.setError(getString(R.string.ERROR_LONG_9));
        }
        if (TextUtils.equals("", user.getFloor())) {
            error = true;
            fragFormRegisterFloor.setError(getString(R.string.ERROR_EMPTY));
        }
        if (TextUtils.equals("", user.getDoor())) {
            error = true;
            fragFormRegisterDoor.setError(getString(R.string.ERROR_EMPTY));
        }
        if (TextUtils.equals("", pass)) {
            error = true;
            fragFormRegisterPin.setError(getString(R.string.ERROR_EMPTY));
        } else if (pass.length() < 6) {
            error = true;
            fragFormRegisterPin.setError(getString(R.string.ERROR_SHORT_6));
        }

        if (!error) {
            createUser(user, pass); //si no hay error , se crea el usuario
        } else {
            activateButton();
        }
    }
}
