interface Patient {
  id: number; 
  name: string;
  age: number;
  gender: string;
  problem: string;
  emergencyLevel: number;
  arrivalTime: string; 
  cured: boolean;
}

export default Patient;