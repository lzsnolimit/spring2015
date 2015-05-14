(* Zhongshan Lu                                                    
   University of Wyoming, Department of Computer Science, Laramie WY 
   COSC 4780 -- Principles of Programming Languages -- Spring 2015
*)

(* base code for HW 10 *)

(* ====================================================================== *)
(* UTILITIES                                                              *)
(* ====================================================================== *)

let update (x,v) f = fun y -> if x = y then v else f y ;;

let rec map f l = 
  match l with 
      [] -> []
    | h::t -> (f h)::(map f t)
;;

let rec member x l =
  match l with
      [] -> false
    | h::t -> if h = x then true else member x t
;;

let rec disjoint l m = 
  match l with
      [] -> true
    | h::t -> not (member h m) && (disjoint t m)
;;

let disjoint_union l m = 
 if disjoint l m then 
   (l @ m)
 else
   raise (Failure "disjoint_union: not disjoint.")
;;


(* ====================================================================== *)
(* ABSTRACT SYNTAX                                                        *)
(* ====================================================================== *)

(* ---------------------------------------------------------------------- *)
(* locations                                                              *)
(* ---------------------------------------------------------------------- *)

type loc =  Loc of int ;;

let well_typed_loc (Loc i) = i > 0 ;;

let string_of_loc l = 
  match l  with
   Loc i -> "Loc " ^ (string_of_int i)
;;

(* ---------------------------------------------------------------------- *)
(* expressions                                                            *)
(* ---------------------------------------------------------------------- *)

type expression =
    Num of int
  | Deref of loc
  | Plus of expression * expression
  | Not of expression
  | Eq of expression * expression
  | Funcall of string
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

(* ---------------------------------------------------------------------- *)
(* declarations                                                           *)
(* ---------------------------------------------------------------------- *)

type declaration = 
    Fun of string * expression 
  | Comma of declaration * declaration 
  | Semi of declaration * declaration
;;

(* ---------------------------------------------------------------------- *)
(* programs                                                               *)
(* ---------------------------------------------------------------------- *)

type program = Prog of declaration * command;;

(* ====================================================================== *)
(* TYPE CHECKING                                                          *)
(* ====================================================================== *)

(* ---------------------------------------------------------------------- *)
(* type assignments                                                       *)
(* ---------------------------------------------------------------------- *)

type expression_types = Boolexp | Intexp ;;

let string_of_expression_types t = 
  match t with
      Boolexp -> "Boolexp"
    | Intexp -> "Intexp"
;;

exception TypeError of expression ;;

type type_assignment = TA of ((string * expression_types) list) ;;

let update_ta (i,v) (TA f) = TA ((i,v)::f);;

let rec type_of_id (TA f) id = 
  match f with
      [] -> (raise (Failure ("no such type assignment: " ^ id))  : expression_types)
    | (id',ty)::t -> if id = id' then ty else (type_of_id (TA t) id)
;;

let ta_names (TA f) = map fst f;;

let union_ta (TA p1) (TA p2) = 
  if (disjoint (ta_names (TA p1)) (ta_names (TA p2))) then 
    TA (p1 @ p2)
  else 
    raise (Failure "union_ta: undefined -- not disjoint.")
;;


(* pi0 is the empty type assignment *)
let pi0  = TA [] ;;

let print_ta (TA f) =
  let rec pta f =
    match f with
	[] -> ()
      | h::t -> 
	  (print_string (fst h);
	   print_string ":"; 
           print_string (string_of_expression_types (snd h));
           (if not (t = []) then 
	      print_string ", "
	    else
	      ());
	   pta  t
	  )

  in
    print_string "{";
    pta f;
    print_string "}"
;;



(* ---------------------------------------------------------------------- *)
(* typing expressions                                                     *)
(* ---------------------------------------------------------------------- *)

let rec expression_type e pi =
  match e with 
    Num (i) -> Intexp
  | Deref (al) -> 
      if (well_typed_loc al) then Intexp else raise (TypeError e)
  | Plus (e1,e2) -> 
      if ((expression_type e1 pi = Intexp) & (expression_type e2 pi = Intexp)) then 
	Intexp
      else
	raise(TypeError e)
  | Not (e1) ->   
      if (expression_type e1 pi = Boolexp) then Boolexp else raise(TypeError e)
  | Eq (e1,e2) -> 
      let e1t = (expression_type e1 pi) in
      let e2t = (expression_type e2 pi) in
      if (e1t = e2t) then Boolexp else raise(TypeError e)
  | Funcall name -> type_of_id pi name
;;

let well_typed_expression e pi = 
  try (let _  = expression_type e pi in true ) with TypeError e -> false 
;;

(* ---------------------------------------------------------------------- *)
(* typing declarations                                                    *)
(* ---------------------------------------------------------------------- *)

let rec declaration_type d pi =
  match d with
    Fun (id, body) -> update_ta (id, expression_type body pi) pi0
  | Comma (d1,d2) -> union_ta (declaration_type d1 pi) (declaration_type d2 pi)
  | Semi (d1, d2) -> let pi1 = (declaration_type d1 pi) in
                     let pi2 = declaration_type d2 (union_ta pi pi1) in 
		       union_ta pi1 pi2
;;

let well_typed_declaration d pi =
  try (let _ = declaration_type d pi in true ) with TypeError e -> false | _ -> false  
;;
  
(* ---------------------------------------------------------------------- *)
(* typing commands                                                        *)
(* ---------------------------------------------------------------------- *)

let rec well_typed_command c pi =
  match c with 
    Assign (al,e) -> 
      (well_typed_loc al) 
	& (well_typed_expression e pi) 
	& (expression_type e pi = Intexp)
  | Seq (c1,c2) -> (well_typed_command c1 pi) & (well_typed_command c2 pi)
  | If (e,c1,c2) -> 
      (well_typed_expression e pi) 
	& (expression_type e pi = Boolexp) 
	& (well_typed_command c1 pi) 
	& (well_typed_command c2 pi)
  | While (e,c1) -> 
      (well_typed_expression e pi) 
	& (expression_type e pi = Boolexp) 
	& (well_typed_command c1 pi)
  | Skip  -> true
;;

(* ====================================================================== *)
(* SEMANTICS                                                              *)
(* ====================================================================== *)

(* ---------------------------------------------------------------------- *)
(* stores                                                                 *)
(* ---------------------------------------------------------------------- *)

type store = Store of  (int * (loc -> int)) ;;
let size (Store (i,f)) =  i;;
let mem (Store (i,f)) = f ;;

let lookup ((Loc i) , s) =
  if (i > size s) or (i <= 0) then
    raise (Failure "lookup: address out of range")
  else
    (mem s) (Loc i)
 ;;

let update_store (Loc i, v, s) =
  if i > (size s) or (i < 0) then
    raise (Failure "update_store: address out of range")
  else
    Store (size s, fun x -> if ((Loc i) = x) then v else ((mem s) x))
;;

let allocate  (Store (sz,mem)) = 
  let init = 0 in
  let nsz = sz + 1 in
    (Loc nsz, update_store (Loc nsz, init, Store (nsz, mem)))
;;

let free i (Store (sz,mem)) = 
   if (i < sz & i >= 0) then
     Store(i,mem) 
   else 
     raise (Failure "free: new size must be between 0 and current size.")
;;

(* s0 is the empty store initialized to zero *)
let s0  = Store (0, (fun x -> raise (Failure "addrress out of range."))) ;;
let s5  = Store (5, (fun (Loc x) -> x));;

let print_store_to i (Store (sz,f)) = 
  let rec ps j = 
    if (j <= i) & (j <= sz) then
      (print_string "Loc";
       print_int j ;
       print_string ": ";
       print_int (f (Loc j)); 
       print_string "\n";
       ps (j + 1))
    else
      ()
  in
 ps 1
;;

let print_store s = print_store_to (size s) s;;

(* ---------------------------------------------------------------------- *)
(* environments                                                           *)
(* ---------------------------------------------------------------------- *)

type env = Env of (string -> store -> bool) * (string -> store -> int) ;;

let apply_bool_exp_env (Env (bool_exp_env, _)) =  bool_exp_env;;

let apply_int_exp_env (Env (_, int_exp_env)) =  int_exp_env;;

let update_bool_exp_env (i, v) e = 
  match e with 
    Env (bool_exp_env, int_exp_env) ->  Env (update (i, v) bool_exp_env, int_exp_env)
;;

let update_int_exp_env  (i, v) e =
  match e with 
    Env (bool_exp_env, int_exp_env) ->  Env (bool_exp_env,  update (i, v) int_exp_env)
;;

let union_env (Env (bf1,if1)) (Env (bf2,if2)) = 
  Env ((fun i s -> try (bf1 i s) with _ -> bf2 i s),
       (fun i s -> try (if1 i s) with _ -> if2 i s))
;;

(* env0 is the empty environment *)
let env0 = 
  Env 
    ((fun i-> fun (s : store) -> raise (Failure (i ^ " not in bool environment.")); true),
     (fun i-> fun (s : store) -> raise (Failure (i ^ " not in int environment.")); 0))
;;


let print_env (TA f) e s =
  let rec penv f =
    match f with
	[] -> ()
      | (i,v)::t -> 
	  (print_string i;
	   print_string ":"; 
	   (match v with
		Intexp -> print_int (apply_int_exp_env e i s)
	      | Boolexp -> 
		  if (apply_bool_exp_env e i s) then 
		    (print_string "true")
		  else
		    (print_string "false"));
           (if not (t = []) then 
	      print_string ", "
	    else
	      ());
	   penv  t
	  )

  in
    print_string "{";
    penv f;
    print_string "}"
;;


(* ---------------------------------------------------------------------- *)
(* meaning of locations                                                   *)
(* ---------------------------------------------------------------------- *)

let meaning_of_loc (l:loc) = l ;;

(* ---------------------------------------------------------------------- *)
(* meaning of expresions --                                               *)
(* ---------------------------------------------------------------------- *)

let rec meaning_of_int_expression e pi env s =
  match e with 
    Num i ->  i
  | Deref al -> lookup (meaning_of_loc al, s)
  | Plus (e1,e2) -> (meaning_of_int_expression e1 pi env s) + (meaning_of_int_expression e2 pi env s)
  | Funcall v -> 
      if (type_of_id pi v = Intexp) then
	apply_int_exp_env env v s
      else
-	raise (Failure "meaning_of_int_expression: not an int expression.")
  | _ -> raise (Failure "meaning_of_int_expression: not an int expression.")
;;

let rec meaning_of_bool_expression e pi env s =
  match e with 
    Not e1 -> not (meaning_of_bool_expression e1 pi env s)
  | Eq (e1,e2) -> 
      (match (expression_type e1 pi, expression_type e2 pi) with
	(Boolexp, Boolexp) -> 
	  (meaning_of_bool_expression e1 pi env s)
	    = (meaning_of_bool_expression e2 pi env s)
      | (Intexp, Intexp) -> 
	  (meaning_of_int_expression e1 pi env s)
	    = (meaning_of_int_expression e2 pi env s)
      | _ -> raise (Failure "meaning_of_bool_expression: not a Boolean expression."))
  | Funcall v -> 
      if (type_of_id pi v = Boolexp) then
	apply_bool_exp_env env v s
      else
	raise (Failure "meaning_of_bool_expression: not a Boolean expression.")
  | _ -> raise (Failure "meaning_of_bool_expression: not a Boolean expression.")
;;

(* ---------------------------------------------------------------------- *)
(* meaning of commands                                                    *)
(* ---------------------------------------------------------------------- *)

let rec meaning_of_command c pi env s =
  match c with 
    Assign (al,e) -> update_store (meaning_of_loc al, meaning_of_int_expression e pi env s, s)
  | Seq (c1,c2) -> meaning_of_command c2 pi env (meaning_of_command c1 pi env s)
  | If (e,c1,c2) -> 
      if (meaning_of_bool_expression e pi env s) then 
	(meaning_of_command c1 pi env s)
      else 
	(meaning_of_command c2 pi env s)
  | While (e,c1) -> 
      (let rec w s = 
	if (meaning_of_bool_expression e pi env s) then
	  w (meaning_of_command c1 pi env s)
	else s
      in
        w s)
  | Skip  -> s
;;

(* ---------------------------------------------------------------------- *)
(* meaning of declarations                                                *)
(* ---------------------------------------------------------------------- *)

let rec meaning_of_declaration d pi (e :env) (s:store)  = 
  match d with
      Fun (id, body) -> 
	(match (expression_type body pi) with
	     Intexp -> update_int_exp_env (id, meaning_of_int_expression body pi e) e
	   | Boolexp -> update_bool_exp_env (id, meaning_of_bool_expression body pi e) e
	)
    | Comma (d1,d2) -> union_env (meaning_of_declaration d1 pi e s) (meaning_of_declaration d2 pi e s)
    | Semi (d1,d2) ->  let pi1 = (declaration_type d1 pi) in
                        meaning_of_declaration d2 pi1 (meaning_of_declaration d1 pi e s) s
;;

(* ---------------------------------------------------------------------- *)
(* meaning of programs                                                    *)
(* ---------------------------------------------------------------------- *)

let meaning_of_program (Prog (d,c)) (s : store) =
  let pi = declaration_type d pi0 in
  let e = meaning_of_declaration d pi0 env0 s in
meaning_of_command c pi e s
;;
