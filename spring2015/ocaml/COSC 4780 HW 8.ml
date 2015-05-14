(*  Zhongshan Lu
	 COSC 4780 - HW 8
*)

(* base code for hw8 *)

(* ====================================================================== *)
(* UTILITIES                                                              *)
(* ====================================================================== *)
 

(* compute the intersection of two lists *)

let list_intersection m n = 
  List.fold_right (fun x y -> if List.mem x n then x:: y else y) m [];;

(* ====================================================================== *)
(* ABSTRACT SYNTAX                                                        *)
(* ====================================================================== *)

(* ---------------------------------------------------------------------- *)
(* locations                                                              *)
(* ---------------------------------------------------------------------- *)

type loc =  Loc of int ;;

let string_of_loc l = 
  match l  with
   Loc i -> "Loc " ^ (string_of_int i)
;;

(* ---------------------------------------------------------------------- *)
(* expressions                                                            *)
(* ---------------------------------------------------------------------- *)

type expression =
    N of int
  | Deref of loc
  | Add of expression * expression
  | Neg of expression
  | Eq of expression * expression
;;

let rec string_of_expression e =
  match e with 
    N (i) -> "N " ^ (string_of_int i)
  | Deref (al) -> "Deref " ^ (string_of_loc al)
  | Add (e1,e2) -> "Add (" ^ (string_of_expression e1) ^ ", " ^ (string_of_expression e1) ^ ")"
  | Neg (e1) -> "Neg " ^ (string_of_expression e1)
  | Eq (e1,e2) -> "Eq  (" ^ (string_of_expression e1) ^ ", " ^ (string_of_expression e1) ^ ")"
;;


(* ---------------------------------------------------------------------- *)
(* commands                                                               *)
(* ---------------------------------------------------------------------- *)

type command = 
    Assign of loc * expression
  | Seq of command * command
  | If of expression * command * command
  | While of expression * command
  | Skip
;;

let rec string_of_command c = 
  match c with 
    Assign (al,e) -> "Assign (" ^ (string_of_loc al) ^ ", " ^ (string_of_expression e) ^ ")"
  | Seq (c1,c2) -> "Seq (" ^ (string_of_command c1) ^ ", " ^ (string_of_command c2) ^ ")"
  | If (e,c1,c2) -> "If (" ^ (string_of_expression e) ^ ", " ^ (string_of_command c1) ^ ", " ^ (string_of_command c2) ^ ")"
  | While (e,c1) -> "While (" ^ (string_of_expression e) ^ ", " ^ (string_of_command c1) ^ ")"
  | Skip  -> "Skip "
;;


(* ====================================================================== *)
(* ANALYSIS                                                               *)
(* ====================================================================== *)

(* ---------------------------------------------------------------------- *)
(* reports                                                                *)
(* ---------------------------------------------------------------------- *)

type report = Good | Possible of (command list);;

let union_reports r1 r2 = 
  match r1 with
      Good -> r2
    | Possible c1 -> (match r2 with
			  Good -> r1
			| Possible c2 -> Possible (c1 @ c2))
		      ;;

(* ---------------------------------------------------------------------- *)
(* the analysis                                                           *)
(* ---------------------------------------------------------------------- *)

(* implement the following functions *)
let rec exp_refs e = 
    match e with
	N (i) -> []
  | Deref (al) -> [al]
  | Add (e1,e2) -> (exp_refs e1)@(exp_refs e2) 
  | Neg (e1) -> exp_refs e1
  | Eq (e1,e2) ->  (exp_refs e1)@(exp_refs e2)
;;

let rec active_locs c =  
  match c with
    Assign (al,e) -> [al]
  | Seq (c1,c2) -> (active_locs c1)@(active_locs c2)
  | If (e,c1,c2) -> (active_locs c1)@(active_locs c2)
  | While (e,c1) -> active_locs c1
  | Skip -> []
  ;;
  
let rec loops_analysis c =  
	match c with
    Assign (al,e) -> Good
  | Seq (c1,c2) -> union_reports (loops_analysis c1) (loops_analysis c2)
  | If (e,c1,c2) -> union_reports (loops_analysis c1) (loops_analysis c2)
  | While (e,c1) -> if list_intersection (exp_refs e) (active_locs c1) = [] then union_reports (Possible [c]) (loops_analysis c1) else union_reports (Good) (loops_analysis c1)
  | Skip -> Good
  ;;