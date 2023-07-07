import logo from "../assets/images/logo/logo.svg";
import { Link } from "react-router-dom";

interface LogoProps {
  center?: boolean;
  noMargin?: boolean;
  widthFix?: boolean;
}

const Logo = ({ center, noMargin, widthFix }: LogoProps) => {
  let className = "nav";
  if (center) {
    className += " center";
  }
  if (noMargin) {
    className += " noMargin";
  }
  if (widthFix) {
    className += " widthFix";
  }
  return (
    <Link className={className} to="/">
      <img src={logo} alt="logo image" />
      <h2>Log Book</h2>
    </Link>
  );
};
export default Logo;
