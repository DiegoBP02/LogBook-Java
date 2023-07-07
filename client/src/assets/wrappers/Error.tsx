import styled from "styled-components";

const Wrapper = styled.main`
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  h2 {
    margin-bottom: 0.5rem;
  }
  p {
    max-width: none;
    font-size: 1.1rem;
    margin-top: 0;
    margin-bottom: 0.5rem;
  }
  img {
    max-width: 600px;
    display: block;
    margin-bottom: 2rem;
  }
  .link {
    font-size: 1.25rem;
    text-decoration: underline;
    color: var(--primary);
  }
`;

export default Wrapper;
