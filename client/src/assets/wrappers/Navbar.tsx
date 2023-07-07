import styled from "styled-components";

const Wrapper = styled.nav`
  height: 6rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .header {
    width: var(--fluid-width);
    max-width: var(--max-width);
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .btns {
    position: relative;
  }
  .btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }
  .logout {
    position: absolute;
    top: 2.5rem;
    width: 100%;
    left: 0;
    visibility: hidden;
  }
  .show-dropdown {
    visibility: visible;
  }
  .logo {
    height: 3.5rem;
  }
  @media (max-width: 768px) {
    h2 {
      font-size: 1.75rem;
    }
  }
`;

export default Wrapper;
