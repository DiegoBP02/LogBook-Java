import styled from "styled-components";

const Wrapper = styled.section`
  display: grid;
  align-items: center;
  max-width: var(--max-width);
  margin: 0 auto;
  text-align: center;

  .member-btn {
    background: transparent;
    border: transparent;
    color: var(--primary);
    cursor: pointer;
  }

  .logo {
    height: 4rem;
    margin-bottom: 2rem;
  }
  .mt {
    margin-top: 1.5rem;
  }
`;

export default Wrapper;
