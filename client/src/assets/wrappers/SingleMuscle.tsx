import styled from "styled-components";

const Wrapper = styled.div`
  width: var(--fluid-width);
  max-width: var(--max-width);
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
  gap: 1rem;
  margin-top: 4rem;
  .singleWorkout {
    height: 130px;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    border-radius: var(--borderRadius);
    flex-direction: column;
    cursor: pointer;
    text-decoration: none;
    position: relative;
    background-color: #d0d0d0;
  }
  .addWorkout {
    background-color: #999999;
    border: 3px solid #000000;
  }
  .addWorkout b {
    font-size: 3rem;
  }
`;

export default Wrapper;
