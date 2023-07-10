import styled from "styled-components";

const Wrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: grid;
  place-items: center;
  transition: var(--transition);
  .formWrapper {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    background-color: #dfdfdf;
    padding: 2rem;
    border-radius: 1rem;
    gap: 1rem;
    position: relative;
  }
  .formWrapper h5 {
    margin-bottom: 0;
  }
  .closeButton {
    align-self: flex-end;
    background: none;
    box-shadow: none;
    color: red;
    font-weight: 900;
    position: absolute;
    top: 0.375rem;
    right: 0.375rem;
  }
  .dateInput:focus {
    outline: none;
    border: 1.5px solid #000;
  }
  .title {
    display: flex;
  }
  .checkboxWrapper {
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .checkboxWrapper input {
    margin-right: 0.25rem;
  }
`;

export default Wrapper;
