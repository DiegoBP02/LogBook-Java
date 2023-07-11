import styled from "styled-components";

const Wrapper = styled.div`
  width: var(--fluid-width);
  max-width: var(--max-width);
  margin: 0 auto;
  .form {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    gap: 1rem;
    padding: 0.5rem 0.625rem;
    background-color: #e0e0e0;
    position: relative;
    margin-bottom: 5rem;
  }
  .formWrapper {
    display: flex;
    gap: 1rem;
  }
  .btn {
    width: fit-content;
  }
  .removeBtn {
    position: absolute;
    bottom: -2rem;
  }
  .properties {
    display: grid;
    place-items: center;
    margin: 0 auto;
    grid-template-columns: 1fr 3fr 1fr 1fr 1fr 50px;
    border-bottom: 1px solid #d0d0d0;
  }
  .properties p {
    margin: 0;
  }
`;

export default Wrapper;
