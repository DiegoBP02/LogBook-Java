import styled from "styled-components";

const Wrapper = styled.main`
  width: var(--fluid-width);
  max-width: var(--max-width);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  .muscles {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1rem;
    margin-top: 4rem;
  }
  .singleMuscle {
    height: 180px;
    border-radius: var(--borderRadius);
    border: 1.5px solid var(--primary);
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    font-size: 1.5rem;
    font-weight: 600;
    cursor: pointer;
    color: var(--primary);
  }
  .singleMuscle img {
    height: 50%;
    width: 50%;
    margin-top: 1rem;
  }
  .languageBtn {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    align-self: flex-end;
  }
  .flagImg {
    width: 30px;
    height: 15px;
  }
`;

export default Wrapper;
