import styled from "styled-components";

const Wrapper = styled.main`
  .container {
    min-height: calc(100vh - 6rem);
    display: grid;
    align-items: center;
  }
  .page {
    width: var(--fluid-width);
    max-width: var(--max-width);
    margin: 0 auto;
  }
  .img {
    display: none;
    width: 100%;
  }
  h1 {
    font-weight: bold;
    color: var(--primary);
  }
  .nav img {
    height: 3.5rem;
  }
  @media (min-width: 992px) {
    .container {
      grid-template-columns: 1fr 1fr;
      column-gap: 5rem;
    }
    .img {
      display: block;
    }
  }
`;

export default Wrapper;
