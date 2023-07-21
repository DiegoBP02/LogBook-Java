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
    margin: 2rem auto;
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
    grid-template-columns: 1fr 2fr 2fr 1fr 1fr 25px 25px;
    border-bottom: 1px solid #d0d0d0;
  }
  .properties p {
    margin: 0;
  }
  .propertiesUniqueExercises {
    display: grid;
    place-items: center;
    margin: 0 auto;
    grid-template-columns: 1fr 1fr;
    border-bottom: 1px solid #d0d0d0;
    align-items: start;
  }

  .propertiesUniqueExercises p {
    text-align: center;
    overflow-x: auto;
    max-width: 150px;
  }

  .properties input {
    max-width: 100%;
    width: 3.5rem;
    text-align: center;
    height: 1.5rem;
  }

  .editInput {
    cursor: pointer;
    color: green;
  }

  .center {
    display: flex;
    justify-content: center;
    gap: 1rem;
  }

  .nameInput {
    overflow-x: auto;
    max-width: 15rem;
  }

  @media (max-width: 992px) {
    .nameInput {
      max-width: 100px;
    }
  }
`;

export default Wrapper;
