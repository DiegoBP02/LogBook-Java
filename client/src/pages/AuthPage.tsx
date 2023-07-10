import { useState, useEffect } from "react";
import { Logo, FormRow, Alert } from "../components";
import Wrapper from "../assets/wrappers/Register";
import { useAppContext } from "../context/appContext";
import { useNavigate } from "react-router-dom";

interface InitialStateProps {
  username: string;
  email: string;
  password: string;
  isMember: boolean;
}

const initialState: InitialStateProps = {
  username: "",
  email: "",
  password: "",
  isMember: true,
};

function Register() {
  const { showAlert, displayAlert, setupUser, isLoading, userToken } =
    useAppContext();

  const navigate = useNavigate();

  const [values, setValues] = useState<InitialStateProps>(initialState);

  const toggleMember = () => {
    setValues({ ...values, isMember: !values.isMember });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const { username, email, password, isMember } = values;
    if (!username || !password || (!isMember && !username)) {
      displayAlert();
      return;
    }

    const currentUser = { username, email, password };
    if (isMember) {
      setupUser(currentUser, "login", "Login successful! Redirecting...");
    } else {
      setupUser(currentUser, "register", "User created! Redirecting...");
    }
  };

  useEffect(() => {
    if (userToken) {
      setTimeout(() => {
        navigate("/");
      }, 3000);
    }
  }, [userToken, navigate]);

  return (
    <Wrapper className="full-page ">
      <form className="form" onSubmit={onSubmit}>
        <Logo center widthFix />
        <h3> {values.isMember ? "Login" : "Register"}</h3>
        {showAlert && <Alert />}
        {/* name field */}
        <FormRow
          type="text"
          name="username"
          value={values.username}
          handleChange={handleChange}
        />
        {/* email field */}
        {!values.isMember && (
          <FormRow
            name="email"
            type="email"
            value={values.email}
            handleChange={handleChange}
          />
        )}
        {/* password field */}
        <FormRow
          name="password"
          type="password"
          value={values.password}
          handleChange={handleChange}
        />

        <button type="submit" className="btn btn-block mt" disabled={isLoading}>
          submit
        </button>
        <p>
          {values.isMember ? "Not a member yet?" : "Already a member?"}
          <button type="button" className="member-btn " onClick={toggleMember}>
            {values.isMember ? "Register" : "Login"}
          </button>
        </p>
      </form>
    </Wrapper>
  );
}

export default Register;
