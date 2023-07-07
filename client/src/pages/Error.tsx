import { Link } from "react-router-dom";
import img from "../assets/images/error/not-found.svg";
import Wrapper from "../assets/wrappers/Error";

const Error = () => {
  return (
    <Wrapper className="full-page">
      <div>
        <img src={img} alt="not found" />
        <h2>Oops! Page Not Found!</h2>
        <p>We can't seem to find the page you're looking for</p>
        <Link to="/" className="link">
          Back to home
        </Link>
      </div>
    </Wrapper>
  );
};

export default Error;
