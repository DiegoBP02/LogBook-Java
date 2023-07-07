import Wrapper from "../assets/wrappers/Landing";
import { Link } from "react-router-dom";
import landing from "../assets/images/landing/landing.svg";
import Logo from "../components/Logo";

const Landing = () => {
  return (
    <>
      <Wrapper>
        <Logo />
        <div className="container page">
          <div className="info">
            <h1>Log Book</h1>
            <p>
              Lorem ipsum dolor sit amet consectetur, adipisicing elit. Neque
              aliquid voluptates, explicabo rem nesciunt eum consequuntur
              consectetur veritatis optio amet sunt.
            </p>
            <Link to="/register" className="btn btn-hero">
              Login / Register
            </Link>
          </div>
          <img src={landing} alt="log book landing" className="img" />
        </div>
      </Wrapper>
    </>
  );
};

export default Landing;
