import Wrapper from "../assets/wrappers/Navbar";
import { FaUserCircle } from "react-icons/fa";
import { RiArrowDownSFill } from "react-icons/ri";
import { useState } from "react";
import Logo from "./Logo";
import { useAppContext } from "../context/appContext";

const Navbar = () => {
  const { logoutUser, username } = useAppContext();
  const [showLogout, setShowLogout] = useState(false);

  const firstName = username.split(" ")[0];

  return (
    <Wrapper>
      <header className="header">
        <Logo noMargin />
        <div className="btns">
          <button className="btn" onClick={() => setShowLogout(!showLogout)}>
            <FaUserCircle />
            {firstName}
            <RiArrowDownSFill />
          </button>
          <button
            className={showLogout ? "btn logout show-dropdown" : "btn logout "}
            onClick={logoutUser}
          >
            Logout
          </button>
        </div>
      </header>
    </Wrapper>
  );
};

export default Navbar;
