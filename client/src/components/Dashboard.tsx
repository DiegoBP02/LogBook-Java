import { Link } from "react-router-dom";
import bicepsImg from "../assets/images/muscles/biceps.svg";
import Wrapper from "../assets/wrappers/Dashboard";
import { Loading } from ".";

const Dashboard = () => {
  const isLoading: boolean = false; // temporary

  const muscles = [
    // temporary
    { name: "biceps", _id: 1 },
    { name: "triceps", _id: 2 },
  ];

  return (
    <Wrapper>
      {isLoading ? (
        <Loading />
      ) : (
        <>
          <main className="muscles">
            {muscles.map(({ name: muscle, _id: muscleId }, index) => {
              return (
                <Link
                  to={`/singleMuscle/${muscleId}`}
                  className="singleMuscle"
                  key={index}
                >
                  {muscle}
                  <img src={bicepsImg} alt="" />
                </Link>
              );
            })}
          </main>
        </>
      )}
    </Wrapper>
  );
};

export default Dashboard;
