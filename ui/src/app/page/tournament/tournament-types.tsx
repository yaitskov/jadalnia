import { BasicTourInfo } from 'app/page/tournament/basic-tournament-info';

export interface TournamentRules {
}

export interface TennisTournamentRules extends TournamentRules {
}

export const newTennisTourRules = () => ({

});

export interface NewTournament<T extends TournamentRules> {
  basic: BasicTourInfo;
  rules: T;
}
